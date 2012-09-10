package com.sfeir.githubTrello.service;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.ImmutableMap;
import com.sfeir.githubTrello.domain.github.Branch;
import com.sfeir.githubTrello.domain.github.Repository;
import com.sfeir.githubTrello.wrapper.RestClient;

import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.lang.String.*;
import static org.apache.commons.lang3.StringUtils.*;

public class GithubService {

	public GithubService(Repository repository, String token) {
		this.repository = repository;
		this.rest = new RestClient(API_URL, format("&access_token=%s", token));
		//TODO: Bof
		baseBranch = getBranch(repository.getBaseBranchName());
		if (!baseBranch.exists()) {
			logger.warn(format("Originating commit for branch %s not found", baseBranch));
		}
	}


	public final Branch getBranch(String branchName) {
		String branchJson = rest.url("/repos/%s/%s/branches/%s", repository.getUser(), repository.getName(), branchName).get();
		return fromJsonToObject(branchJson, Branch.class);
	}

	public final String createFeatureBranch(String branchName) {
		Map<String, String> input =
				ImmutableMap.of("ref", "refs/heads/" + branchName, "sha", baseBranch.getCommit().getSha());
		String branch = rest.url("/repos/%s/%s/git/refs", repository.getUser(), repository.getName()).post(input);
		logger.info(format("Feature branch %s created with output %s", branchName, branch));
		return branch;
	}

	public final String createPullRequest(String title, String body, Branch branch) {
		String head = format("%s:%s", repository.getUser(), branch.getName());
		Map<String, String> input =
				ImmutableMap.of("title", title, "body", body, "head", head, "base", baseBranch.getName());
		String pullRequest = rest.url("/repos/%s/%s/pulls", repository.getUser(), repository.getName()).post(input);
		if (isNotEmpty(pullRequest)) {
			logger.info(format("Pull request for branch %s created, output %s", branch.getName(), pullRequest));
		}
		return pullRequest;
	}

	public final boolean hasCommitsOnBranch(Branch branch) {
		return !baseBranch.getCommit().getSha().equals(branch.getCommit().getSha());
	}

	protected RestClient rest;
	protected Repository repository;
	private Branch baseBranch;
	private static final Log logger = LogFactory.getLog(GithubService.class);
	private static final String API_URL = "https://api.github.com";

}