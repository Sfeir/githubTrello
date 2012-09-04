package com.sfeir.githubTrello.domain.github;

public class Repository {

	private String user;
	private String name;
	private String baseBranch;


	public String getUser() {
		return user;
	}

	public String getName() {
		return name;
	}

	public String getBaseBranchName() {
		return baseBranch;
	}

	public static Builder repositoryBuilder() {
		return new Repository.Builder();
	}

	public static class Builder {

		public Builder user(String user) {
			this.user = user;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder baseBranch(String baseBranch) {
			this.baseBranch = baseBranch;
			return this;
		}

		public Repository build() {
			Repository repository = new Repository();
			repository.user = user;
			repository.name = name;
			repository.baseBranch = baseBranch;
			return repository;
		}

		private String user;
		private String name;
		private String baseBranch;
	}
}
