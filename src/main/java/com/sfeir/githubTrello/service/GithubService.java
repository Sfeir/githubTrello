package com.sfeir.githubTrello.service;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;

import com.google.common.collect.ImmutableMap;
import com.sfeir.githubTrello.wrapper.Rest;

import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.lang.String.*;

public class GithubService {

	public String createFeatureBranch(String branchName) {
		Map<String, ?> input = ImmutableMap.of("ref", "refs/heads/" + branchName, "sha", baseBranchSha);
		String branch = rest.url("/repos/%s/%s/git/refs", user, repository).post(input);
		logger.info(format("Feature branch %s created with output %s", branchName, branch));
		return branch;
	}

	private void setBaseBranchSha(String baseBranch) {
		baseBranchSha = "";
		String source = rest.url("/repos/%s/%s/git/refs/heads/%s", user, repository, baseBranch).get();
		JsonNode node = fromJsonToObject(source, JsonNode.class);
		if (node == null) {
			logger.info(format("Originating commit for base branch %s not found", baseBranch));
		}
		else {
			baseBranchSha = node.get("object").get("sha").getTextValue();
		}
	}

	public Rest getRestWrapper() {
		return rest;
	}

	public static Builder githubServiceBuilder() {
		return new GithubService.Builder();
	}

	public static class Builder {
		private String token;
		private String user;
		private String repository;
		private String baseBranch;

		public Builder token(String token) {
			this.token = token;
			return this;
		}

		public Builder user(String user) {
			this.user = user;
			return this;
		}

		public Builder repository(String repository) {
			this.repository = repository;
			return this;
		}

		public Builder baseBranch(String baseBranch) {
			this.baseBranch = baseBranch;
			return this;
		}

		public GithubService build() {
			GithubService githubService = new GithubService();
			githubService.rest = new Rest(API_URL, format("&access_token=%s", token));
			githubService.user = user;
			githubService.repository = repository;
			githubService.setBaseBranchSha(baseBranch);
			return githubService;
		}
	}

	private Rest rest;
	private String user;
	private String repository;
	private String baseBranchSha;
	private static final Log logger = LogFactory.getLog(GithubService.Builder.class);
	private static final String API_URL = "https://api.github.com";
}
