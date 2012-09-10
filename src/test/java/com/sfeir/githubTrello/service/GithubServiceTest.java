package com.sfeir.githubTrello.service;

import java.util.Collection;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sfeir.githubTrello.domain.github.Branch;
import com.sfeir.githubTrello.domain.github.Branch.Commit;
import com.sfeir.githubTrello.domain.github.Repository;

import static com.google.common.collect.ImmutableMap.*;

import static com.google.common.collect.Lists.*;

import static com.sfeir.githubTrello.domain.github.Repository.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.lang.String.*;
import static java.lang.System.*;
import static org.fest.assertions.Assertions.*;

public class GithubServiceTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		githubRepository = repositoryBuilder()
				.baseBranch(DEVELOP_BRANCH)
				.user(GITHUB_USER)
				.name(GITHUB_REPOSITORY_NAME)
				.build();
		service = new ExpandedGithubService(githubRepository, GITHUB_TOKEN);

		service.deleteBranch(service.getBranch(TEMP_BRANCH));
		assertThat(service.getBranch(MASTER_BRANCH).exists()).isTrue();
		assertThat(service.getBranch(DEVELOP_BRANCH).exists()).isTrue();
		assertThat(service.getAllBranches()).hasSize(2);
	}

	@Test
	public void should_create_one_branch() {
		service.createFeatureBranch(TEMP_BRANCH);
		assertThat(service.getBranch(TEMP_BRANCH).getName()).isEqualTo(TEMP_BRANCH);
	}

	//	@Test
	public void should_not_create_two_identical_pull_requests() {
		String branchName = TEMP_BRANCH + nanoTime();
		service.createFeatureBranch(branchName);

		Branch branch = service.getBranch(branchName);
		Commit latestCommit = branch.getCommit();

		service.commitOneFile(latestCommit, "README.md", "Hello World!", "Dummy commit for branch" + branchName);

		String firstTry = service.createPullRequest(branch.getName(), latestCommit.getSha(), branch);
		String secondTry = service.createPullRequest(branch.getName(), latestCommit.getSha(), branch);


		//		assertThat(firstTry).isNotEmpty();
		//		assertThat(secondTry).isEmpty();


	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		for (Branch branch : service.getAllBranches()) {
			switch (branch.getName()) {
			case MASTER_BRANCH:
				break;
			case DEVELOP_BRANCH:
				break;
			default:
				service.deleteBranch(branch);
			}
		}
	}

	private static Repository githubRepository;
	private static ExpandedGithubService service;

	private static final String TEMP_BRANCH = "temp-branch";
	private static final String MASTER_BRANCH = "master";
	private static final String DEVELOP_BRANCH = "develop";
	private static final String GITHUB_REPOSITORY_NAME = "dummy";
	private static final String GITHUB_USER = "GithubTrello";
	private static final String GITHUB_TOKEN = "eb7e0c3f46b3a3d366acf46d3d4f61989793c370";


	private static class ExpandedGithubService extends GithubService {
		ExpandedGithubService(Repository repository, String token) {
			super(repository, token);
		}

		Collection<Branch> getAllBranches() {
			return fromJsonToObjects(rest.url("/repos/%s/%s/branches", repository.getUser(), repository.getName()).get(), Branch.class);
		}

		String deleteBranch(Branch branch) {
			return rest.url("/repos/%s/%s/git/refs/heads/%s", repository.getUser(), repository.getName(), branch.getName()).delete();
		}

		@SuppressWarnings("unchecked")
		String commitOneFile(Commit parentCommit, String filename, String newContent, String commitMessage) {//Cf http://developer.github.com/v3/git/

			String commitInfo = rest.url(parentCommit.getUrl()).get();
			String treeInfo = rest.url(extractValue(commitInfo, "commit", "tree", "url")).get();

			Map<String, String> newBlob = of("content", newContent, "encoding", "utf-8");
			String blobInfo = rest.url("/repos/%s/%s/git/blobs", repository.getUser(), repository.getName()).post(newBlob);

			Map<String, ?> newTree = of("base_tree", extractValue(commitInfo, "commit", "tree", "sha"),
					"tree", newArrayList(of(
							"path", filename,
							"mode", extractValue(treeInfo, "tree", format("mode[path=%s]", filename)),
							"type", extractValue(treeInfo, "tree", format("type[path=%s]", filename)),
							"sha", extractValue(blobInfo, "sha"))));
			String newTreeInfo = rest.url("/repos/%s/%s/git/trees", repository.getUser(), repository.getName()).post(newTree);

			Map<String, ?> newCommit = of("message", commitMessage, "tree", extractValue(newTreeInfo, "sha"), "parents", newArrayList(parentCommit.getSha()));
			return rest.url("/repos/%s/%s/git/commit", repository.getUser(), repository.getName()).post(newCommit);
		}
	}
}
