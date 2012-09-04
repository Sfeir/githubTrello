package com.sfeir.githubTrello.service;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.ImmutableMap;
import com.sfeir.githubTrello.domain.github.Repository;
import com.sfeir.githubTrello.wrapper.Rest;
import com.sfeir.githubTrello.wrapper.Rest.RestUrl;

import static com.google.common.base.Strings.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.lang.String.*;

public class GithubService {

	public GithubService(Repository repository, String token) {
		this.repository = repository;
		this.rest = new Rest(API_URL, format("&access_token=%s", token));
	}

	public String createFeatureBranch(String branchName) {
		Map<String, ?> input = ImmutableMap.of(
				"ref", "refs/heads/" + branchName,
				"sha", getBranchSha(repository.getBaseBranchName()));
		String branch = rest.url("/repos/%s/%s/git/refs", repository.getUser(), repository.getName()).post(input);
		logger.info(format("Feature branch %s created with output %s", branchName, branch));
		return branch;
	}

	protected RestUrl getRestUrlForBranches(String branchName) {
		return rest.url("/repos/%s/%s/git/refs/heads/%s", repository.getUser(), repository.getName(), nullToEmpty(branchName));
	}

	protected String getBranch(String branchName) {
		return getRestUrlForBranches(branchName).get();
	}

	private String getBranchSha(String baseBranchName) {
		if (baseBranchSha == null) {
			baseBranchSha = extractValue(getBranch(baseBranchName), "object", "sha");
			if (isNullOrEmpty(baseBranchSha)) {
				logger.warn(format("Originating commit for branch %s not found", baseBranchName));
			}
		}
		return baseBranchSha;
	}

	protected Rest rest;
	protected Repository repository;
	private String baseBranchSha;
	private static final Log logger = LogFactory.getLog(GithubService.class);
	private static final String API_URL = "https://api.github.com";
}
