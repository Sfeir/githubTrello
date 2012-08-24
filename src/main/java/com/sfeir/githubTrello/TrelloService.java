package com.sfeir.githubTrello;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.List;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

import static com.sfeir.githubTrello.Json.*;
import static com.sfeir.githubTrello.domain.trello.List.*;
import static java.lang.String.*;

public class TrelloService {

	public TrelloService(String token) {
		this.token = token;
	}

	public List getListWithCards(String initialListId) {
		String cardsInJson = get(format("/lists/%s/cards", initialListId));
		return listBuilder().id(initialListId).cardsInJson(cardsInJson).build();
	}

	public String getListId(Board board, String listName) {
		String listInJson = get(format("/boards/%s/lists", board.getId()));
		for (List list : fromJson(listInJson).to(List.class))
			if (listName.equals(list.getName()))
				return list.getId();
		return "-1";
	}

	private String get(String url) {
		ClientResponse clientResponse = Client.create().resource(appendKeyTokenQuery(url)).accept("application/json")
				.get(ClientResponse.class);
		return clientResponse.getEntity(String.class);// TODO: Error case
	}

	private String appendKeyTokenQuery(String path) {
		return format("%s%s?key=%s&token=%s", apiUrl, path, apiKey, token);
	}

	private final String token;

	// TODO: Hardcoded api key
	private static String apiKey = "d0e4aa36488c2e5957da7c3a61a76ff2";
	private static String apiUrl = "https://api.trello.com/1";

}
