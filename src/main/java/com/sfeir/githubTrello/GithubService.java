package com.sfeir.githubTrello;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import com.sfeir.githubTrello.wrapper.Rest;

import static com.sfeir.githubTrello.wrapper.Escape.*;
import static java.lang.String.*;

public class GithubService {

	public String createFeatureBranch(String newBranch) {
		String refsUrl = format("/repos/%s/%s/git/refs", user, repository);
		String headBranch = escape(newBranch);
		String input = format("{\"ref\": \"refs/heads/%s\",\"sha\": \"%s\"}", headBranch, baseBranchSha);
		String branch = rest.post(refsUrl, input);
		logger.info(format("Feature branch %s created with output %s", headBranch, branch));
		return branch;
	}

	private void setBaseBranchSha(String baseBranch) {
		baseBranchSha = "";
		try {
			String source = rest.get("/repos/%s/%s/git/refs/heads/%s", user, repository, baseBranch);
			JsonNode node = new ObjectMapper().readValue(source, JsonNode.class);
			if (node == null) {
				logger.info(format("Originating commit for base branch %s not found", baseBranch));
			}
			else {
				baseBranchSha = node.get("object").get("sha").getTextValue();
			}
		}
		catch (IOException e) {
			logger.error(e, e);
		}
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
			githubService.rest = new Rest(API_URL, format("access_token=%s", token));
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
