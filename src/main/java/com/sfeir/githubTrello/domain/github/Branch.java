package com.sfeir.githubTrello.domain.github;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Branch {

	public String getName() {
		return ref.replace("refs/heads/", "");
	}

	public Commit getCommit() {
		return commit;
	}

	public boolean exists() {
		return ref != null && commit != null;
	}

	public Branch() {}

	@JsonProperty("object") private Commit commit;
	@JsonProperty("ref") private String ref;

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