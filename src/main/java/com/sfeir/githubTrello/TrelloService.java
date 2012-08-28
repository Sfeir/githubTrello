package com.sfeir.githubTrello;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;
import com.sfeir.githubTrello.wrapper.Rest;

import static com.sfeir.githubTrello.domain.trello.List.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.lang.String.*;

public class TrelloService {

	public List getListWithCards(String initialListId) {
		String cardsInJson = rest.get("/lists/%s/cards", initialListId);
		return listBuilder().id(initialListId).cardsInJson(cardsInJson).build();
	}

	public String getListId(Board board, String listName) {
		String listInJson = rest.get("/boards/%s/lists", board.getId());
		for (List list : fromJsonToObjects(listInJson, List.class)) {
			if (listName.equals(list.getName())) {
				return list.getId();
			}
		}
		return "-1";
	}

	public Card getCard(String cardId) {
		String cardJson = rest.get("/cards/%s", cardId);
		return fromJsonToObject(cardJson, Card.class);
	}

	public TrelloService(String token) {
		this.rest = new Rest(apiUrl, format("key=%s&token=%s", apiKey, token));
	}

	private Rest rest;
	private static String apiKey = "d0e4aa36488c2e5957da7c3a61a76ff2";
	private static String apiUrl = "https://api.trello.com/1";

}
