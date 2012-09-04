package com.sfeir.githubTrello.service;

import org.codehaus.jackson.JsonNode;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sfeir.githubTrello.wrapper.Rest;

import static com.sfeir.githubTrello.service.GithubService.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static org.fest.assertions.Assertions.*;

public class GithubServiceTest {

	private static final String TEMP_BRANCH = "temp-branch";
	private static final String MASTER_BRANCH = "master";
	private static final String BASE_BRANCH = "develop";
	private static final String GITHUB_REPOSITORY = "github-trello-dummy";
	private static final String GITHUB_USER = "GithubTrello";
	private static GithubService service;
	private static Rest rest;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		service = githubServiceBuilder()
				.token("eb7e0c3f46b3a3d366acf46d3d4f61989793c370")
				.baseBranch(BASE_BRANCH)
				.repository(GITHUB_REPOSITORY)
				.user(GITHUB_USER)
				.build();

		rest = service.getRestWrapper();

		assertThat(getBranch(MASTER_BRANCH)).isNotEmpty();
		assertThat(getBranch(BASE_BRANCH)).isNotEmpty();
		assertThat(fromJsonToObjects(getBranches(), JsonNode.class)).hasSize(2);
	}

	@Test
	public void should_create_one_branch()
	{
		service.createFeatureBranch(TEMP_BRANCH);
		assertThat(getBranch(TEMP_BRANCH)).isNotEmpty();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		deleteBranch(TEMP_BRANCH);
	}

	@Before
	public void setUp() throws Exception {}

	@After
	public void tearDown() throws Exception {}


	private static String getBranches() {
		return rest.url("/repos/%s/%s/git/refs/heads/", GITHUB_USER, GITHUB_REPOSITORY).get();
	}

	private static String getBranch(String branch) {
		return rest.url("/repos/%s/%s/git/refs/heads/%s", GITHUB_USER, GITHUB_REPOSITORY, branch).get();
	}

	private static String deleteBranch(String branch) {
		return rest.url("/repos/%s/%s/git/refs/heads/%s", GITHUB_USER, GITHUB_REPOSITORY, branch).delete();
	}

}
