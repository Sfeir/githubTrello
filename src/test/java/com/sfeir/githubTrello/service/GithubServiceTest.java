package com.sfeir.githubTrello.service;

import org.codehaus.jackson.JsonNode;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sfeir.githubTrello.domain.github.Repository;

import static com.sfeir.githubTrello.domain.github.Repository.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static org.fest.assertions.Assertions.*;

public class GithubServiceTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		githubRepository = repositoryBuilder()
				.baseBranch(DEVELOP_BRANCH)
				.user(GITHUB_USER)
				.name(GITHUB_REPOSITORY_NAME)
				.build();
		service = new ExpandedGithubService(githubRepository, "eb7e0c3f46b3a3d366acf46d3d4f61989793c370");

		assertThat(extractValue(service.getBranch(MASTER_BRANCH), "ref")).endsWith(MASTER_BRANCH);
		assertThat(extractValue(service.getBranch(DEVELOP_BRANCH), "ref")).endsWith(DEVELOP_BRANCH);
		assertThat(fromJsonToObjects(service.getAllBranches(), JsonNode.class)).hasSize(2);
	}

	@Test
	public void should_create_one_branch() {
		service.createFeatureBranch(TEMP_BRANCH);
		assertThat(service.getBranch(TEMP_BRANCH)).isNotEmpty();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		service.deleteBranch(TEMP_BRANCH);
	}

	private static final String TEMP_BRANCH = "temp-branch";
	private static final String MASTER_BRANCH = "master";
	private static final String DEVELOP_BRANCH = "develop";
	private static final String GITHUB_REPOSITORY_NAME = "github-trello-dummy";
	private static final String GITHUB_USER = "GithubTrello";

	private static ExpandedGithubService service;
	private static Repository githubRepository;

	private static class ExpandedGithubService extends GithubService {
		public ExpandedGithubService(Repository repository, String token) {
			super(repository, token);
		}

		String getAllBranches() {
			return getRestUrlForBranches("").get();
		}

		String deleteBranch(String branch) {
			return getRestUrlForBranches(branch).delete();
		}
	}
}
