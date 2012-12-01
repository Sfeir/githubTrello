package com.sfeir.githubTrello.service;

import java.util.Collection;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.sfeir.githubTrello.ApiTests;
import com.sfeir.githubTrello.domain.github.Branch;
import com.sfeir.githubTrello.domain.github.Branch.Commit;
import com.sfeir.githubTrello.domain.github.PullRequest;
import com.sfeir.githubTrello.domain.github.Repository;

import static com.google.common.collect.ImmutableMap.*;

import static com.google.common.collect.Lists.*;

import static com.sfeir.githubTrello.domain.github.Repository.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.util.Arrays.*;
import static org.fest.assertions.Assertions.*;

@Category(ApiTests.class)
public class GithubServiceTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		githubRepository = repositoryBuilder()
				.baseBranch(DEVELOP_BRANCH)
				.user(GITHUB_USER)
				.name(GITHUB_REPOSITORY_NAME)
				.build();
		service = new ExpandedGithubService(githubRepository, GITHUB_TOKEN);

		assertThat(service.getBranch(MASTER_BRANCH).exists()).isTrue();
		assertThat(service.getBranch(DEVELOP_BRANCH).exists()).isTrue();
		assertThat(service.getAllBranches()).hasSize(2);
		assertThat(service.getOpenedPullRequests()).isEmpty();
	}

	@Test
	public void should_create_a_feature_branch() {
		Branch featureBranch = service.createFeatureBranch(FEATURE_BRANCH);
		assertThat(featureBranch.getName()).isEqualTo(FEATURE_BRANCH);
		assertThat(service.hasCommitsOnFeatureBranch(featureBranch)).isFalse();
		assertThat(service.hasNoPullRequestForBranch(featureBranch)).isTrue();
	}

	@Test
	public void should_create_only_one_pull_request_and_update_its_description_correctly() {
		Branch branch = service.createFeatureBranch(PULL_REQUEST);
		String commitResult = service.commitFile(branch, "README.md", "Hello World", "Commit for branch: " + PULL_REQUEST);
		assertThat(commitResult).isNotEmpty();

		branch = service.getBranch(PULL_REQUEST);
		assertThat(service.hasCommitsOnFeatureBranch(branch)).isTrue();

		String pullRequestName = branch.getName() + "-" + System.nanoTime();
		PullRequest pullRequest = service.createPullRequest(pullRequestName, "Nothing", branch);
		assertThat(pullRequest.isValid()).isTrue();
		assertThat(pullRequest.getDescription()).isEqualTo("Nothing");
		assertThat(service.hasNoPullRequestForBranch(branch)).isFalse();

		PullRequest identicalPullRequest = service.createPullRequest(pullRequestName, "Nothing", branch);
		assertThat(identicalPullRequest.isValid()).isFalse();

		PullRequest pullRequestOnSameBranchWithDifferentName = service.createPullRequest(pullRequestName + " but different", "Nothing", branch);
		assertThat(pullRequestOnSameBranchWithDifferentName.isValid()).isFalse();

		PullRequest updatedPullRequest = service.updatePullRequestDescription(pullRequest, "Something");
		assertThat(updatedPullRequest.getDescription()).isEqualTo("Something");
	}


	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		for (Branch branch : service.getAllBranches()) {
			if (!asList(MASTER_BRANCH, DEVELOP_BRANCH).contains(branch.getName()))
				service.deleteBranch(branch);
		}
		for (PullRequest pullRequest : service.getOpenedPullRequests()) {
			service.closePullRequest(pullRequest);
		}
	}

	private static Repository githubRepository;
	private static ExpandedGithubService service;

	private static final String FEATURE_BRANCH = "feature-branch";
	private static final String PULL_REQUEST = "pull-request";
	private static final String MASTER_BRANCH = "master";
	private static final String DEVELOP_BRANCH = "develop";
	private static final String GITHUB_REPOSITORY_NAME = "test";
	private static final String GITHUB_USER = "GithubTrello";
	private static final String GITHUB_TOKEN = "eb7e0c3f46b3a3d366acf46d3d4f61989793c370";

	private static class ExpandedGithubService extends GithubService {
		ExpandedGithubService(Repository repository, String token) {
			super(repository, token);
		}

		Collection<Branch> getAllBranches() {
			return fromJsonToObjects(restClient.url("/repos/%s/%s/git/refs", user, repositoryName).get(), Branch.class);
		}

		String deleteBranch(Branch branch) {
			return restClient.url("/repos/%s/%s/git/refs/heads/%s", user, repositoryName, branch.getName()).delete();
		}

		String closePullRequest(PullRequest pullRequest) {
			return restClient.url("/repos/%s/%s/pulls/%s", user, repositoryName, pullRequest.getId()).post(of("state", "closed"));
		}

		@SuppressWarnings("unchecked")
		String commitFile(Branch branch, String filename, String newContent, String commitMessage) {
			//See: http://developer.github.com/v3/git/ or https://gist.github.com/2935203
			Commit commit = branch.getCommit();
			String commitInfo = restClient.url(commit.getUrl()).get();

			Map<String, ?> newTreeInput = of("base_tree", extractValue(commitInfo, "tree", "sha"),
					"tree", newArrayList(of("path", filename, "mode", "100644", "type", "blob", "content", newContent)));
			String newTreeInfo = restClient.url("/repos/%s/%s/git/trees", user, repositoryName).post(newTreeInput);

			Map<String, ?> newCommitInput = of("message", commitMessage, "tree", extractValue(newTreeInfo, "sha"), "parents",
					newArrayList(commit.getSha()));
			String newCommitInfo = restClient.url("/repos/%s/%s/git/commits", user, repositoryName).post(newCommitInput);

			Map<String, ?> updatedBranchInput = of("sha", extractValue(newCommitInfo, "sha"), "force", true);
			return restClient.url("/repos/%s/%s/git/refs/heads/%s", user, repositoryName, branch.getName()).post(updatedBranchInput);
		}
	}
}
