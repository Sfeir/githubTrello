package com.sfeir.githubTrello.domain.github;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import static org.apache.commons.lang3.StringUtils.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PullRequest {

	public String getHtmlUrl() {
		return htmlUrl;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public Head getHead() {
		return head;
	}

	public boolean isOpen() {
		return "open".equals(state);
	}

	public boolean isClosed() {
		return "closed".equals(state);
	}

	public boolean isValid() {
		return isNotEmpty(id) && isNotEmpty(title) && head != null;
	}

	void setState(String state) {
		if (!"open".equals(state) && !"closed".equals(state)) {
			throw new IllegalArgumentException("State must be 'open' or 'closed': " + state);
		}
		this.state = state;
	}

	private String title;
	@JsonProperty("body") private String description;
	@JsonProperty("html_url") private String htmlUrl;
	@JsonProperty("state") private String state;
	@JsonProperty("number") private String id;
	private Head head;

	@JsonIgnoreProperties(ignoreUnknown = true)
	public class Head {

		public String getName() {
			return name;
		}

		public String getSha() {
			return sha;
		}

		@JsonProperty("ref") private String name;
		private String sha;
	}
}
