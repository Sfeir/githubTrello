package com.sfeir.githubTrello.service;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sfeir.githubTrello.domain.github.Branch;
import com.sfeir.githubTrello.domain.github.PullRequest;
import com.sfeir.githubTrello.domain.github.Repository;
import com.sfeir.githubTrello.wrapper.RestClient;

import static ch.lambdaj.Lambda.*;
import static com.google.common.collect.ImmutableMap.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.lang.String.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.hamcrest.Matchers.*;

public class GithubService {

	public GithubService(Repository repository, String token) {
		this.restClient = new RestClient(API_URL, format("&access_token=%s", token));
		this.user = repository.getUser();
		this.repositoryName = repository.getName();

		//TODO (Mr.MEDDAH at Sep 12, 2012): Bof
		baseBranch = getBranch(repository.getBaseBranchName());
		if (!baseBranch.exists()) {
			logger.warn(format("Originating commit for branch %s not found", baseBranch));
		}
	}

	public final Branch getBranch(String branchName) {
		String branch = restClient.url("/repos/%s/%s/git/refs/heads/%s", user, repositoryName, branchName).get();
		return fromJsonToObject(branch, Branch.class);
	}

	public final Branch createFeatureBranch(String branchName) {
		Map<String, String> input = of("ref", "refs/heads/" + branchName, "sha", baseBranch.getCommit().getSha());
		String branch = restClient.url("/repos/%s/%s/git/refs", user, repositoryName).post(input);
		logger.info(format("Feature branch %s created with output %s", branchName, branch));
		return fromJsonToObject(branch, Branch.class);
	}

	public final PullRequest createPullRequest(String title, String body, Branch branch) {
		String head = format("%s:%s", user, branch.getName());
		Map<String, String> input = of("title", title, "body", body, "head", head, "base", baseBranch.getName());
		String pullRequest = restClient.url("/repos/%s/%s/pulls", user, repositoryName).post(input);
		if (isNotEmpty(pullRequest)) {
			logger.info(format("Pull request for branch %s created, output %s", branch.getName(), pullRequest));
		}
		return fromJsonToObject(pullRequest, PullRequest.class);
	}

	public final PullRequest updatePullRequestDescription(PullRequest pullRequest, String description) {
		String updatedPullRequest = restClient.url("/repos/%s/%s/pulls/%s", user, repositoryName, pullRequest.getId()).post(of("body", description));
		return fromJsonToObject(updatedPullRequest, PullRequest.class);
	}

	public final boolean hasCommitsOnBranch(Branch branch) {
		return !baseBranch.getCommit().getSha().equals(branch.getCommit().getSha());
	}

	public final boolean hasNoPullRequestForBranch(Branch featureBranch) {
		return select(getOpenedPullRequests(), having(on(PullRequest.class).getHead().getName(), equalTo(featureBranch.getName()))).isEmpty();
	}

	protected final Collection<PullRequest> getOpenedPullRequests() {
		return fromJsonToObjects(restClient.url("/repos/%s/%s/pulls", user, repositoryName).get(), PullRequest.class);
	}

	protected RestClient restClient;
	protected String user;
	protected String repositoryName;
	private Branch baseBranch;
	private static final Log logger = LogFactory.getLog(GithubService.class);
	private static final String API_URL = "https://api.github.com";

}