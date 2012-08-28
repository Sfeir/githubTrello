package com.sfeir.githubTrello.wrapper;

import javax.ws.rs.core.Response.Status.Family;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import static java.lang.String.*;

public class Rest {

	public Rest(String apiUrl, String authenticationQuery) {
		this.apiUrl = apiUrl;
		this.authenticationQuery = authenticationQuery;
	}

	public String get(String pathFormat, Object... pathParameters) {
		ClientResponse clientResponse = Client.create()
				.resource(url(pathFormat, pathParameters))
				.accept("application/json")
				.get(ClientResponse.class);
		return getResponseEntity(clientResponse);
	}

	public String post(String path, String input) {
		ClientResponse clientResponse = Client.create()
				.resource(url(path))
				.accept("application/json")
				.post(ClientResponse.class, input);
		return getResponseEntity(clientResponse);
	}

	private static String getResponseEntity(ClientResponse clientResponse) {
		if (clientResponse.getClientResponseStatus().getFamily() != Family.SUCCESSFUL) {
			logger.error("Failed : HTTP error code : " + clientResponse.getStatus());
			return "";
		}
		return clientResponse.getEntity(String.class);
	}

	private String url(String pathFormat, Object... pathsParameters) {
		return apiUrl + format(pathFormat, pathsParameters) + "?" + authenticationQuery;
	}

	private final String apiUrl;
	private final String authenticationQuery;

	private static final Log logger = LogFactory.getLog(Rest.class);
}
