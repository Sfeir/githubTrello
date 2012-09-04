package com.sfeir.githubTrello.service;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;
import com.sfeir.githubTrello.wrapper.Rest;

import static com.sfeir.githubTrello.domain.trello.List.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.lang.String.*;

public class TrelloService {

	public List getList(Board board, String listName) {
		String listInJson = rest.url("/boards/%s/lists", board.getId()).get();
		for (List list : fromJsonToObjects(listInJson, List.class)) {
			if (listName.equals(list.getName())) {
				return list.withNewCardsInJson(rest.url("/lists/%s/cards", list.getId()).get());
			}
		}
		return listBuilder().build();
	}

	public Card getCard(String cardId) {
		String cardJson = rest.url("/cards/%s", cardId).get();
		return fromJsonToObject(cardJson, Card.class);
	}

	public TrelloService(String token) {
		this.rest = new Rest(API_URL, format("&key=%s&token=%s", API_KEY, token));
	}

	public Rest getRestWrapper() {
		return rest;
	}

	private Rest rest;

	private static final String API_URL = "https://api.trello.com/1";
	private static final String API_KEY = "d0e4aa36488c2e5957da7c3a61a76ff2";

}
