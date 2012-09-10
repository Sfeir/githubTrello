package com.sfeir.githubTrello.domain.github;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Branch {

	public String getName() {
		return name;
	}

	public Commit getCommit() {
		return commit;
	}

	public boolean exists() {
		return name != null && commit != null;
	}

	public Branch() {}

	private Commit commit;
	private String name;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class Commit {
		public String getSha() {
			return sha;
		}

		public String getUrl() {
			return url;
		}

		public Commit() {}

		private String sha;
		private String url;
	}
}
