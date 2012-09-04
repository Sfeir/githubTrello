package com.sfeir.githubTrello.wrapper;

import java.util.Map;

import javax.ws.rs.core.Response.Status.Family;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource.Builder;

import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.lang.String.*;
import static javax.ws.rs.core.MediaType.*;
import static org.apache.commons.lang3.StringUtils.*;

public class Rest {

	public Rest(String apiUrl, String authenticationQuery) {
		this.apiUrl = apiUrl;
		this.authenticationQuery = authenticationQuery;//TODO authentication query format
	}

	public RestUrl url(String pathFormat, String... pathParameters) {
		String url = apiUrl + format(pathFormat, (Object[]) pathParameters) +
				(containsNone(pathFormat, "?") ? "?" : "") + authenticationQuery;
		return new RestUrl(url);
	}

	public static class RestUrl {
		public String get() {
			return getResponseEntity(initRequest().get(ClientResponse.class));
		}

		public String post(Map<String, ?> input) {
			return getResponseEntity(initRequest().post(ClientResponse.class, fromObjectToJson(input)));
		}

		public String put() {
			return getResponseEntity(initRequest().put(ClientResponse.class));
		}

		public String delete() {
			initRequest().delete(ClientResponse.class);
			return "";
		}

		private Builder initRequest() {
			return Client.create().resource(url).accept(APPLICATION_JSON);
		}

		private static String getResponseEntity(ClientResponse clientResponse) {
			if (clientResponse.getClientResponseStatus() == null ||
					clientResponse.getClientResponseStatus().getFamily() != Family.SUCCESSFUL) {
				logger.error("Failed : HTTP error code : " + clientResponse.getStatus(), new Exception());
				return "";
			}
			return clientResponse.getEntity(String.class);
		}

		private RestUrl(String url) {
			this.url = url;
		}

		private String url;
	}

	private final String apiUrl;
	private final String authenticationQuery;

	private static final Log logger = LogFactory.getLog(Rest.class);
}
