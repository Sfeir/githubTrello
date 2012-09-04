package com.sfeir.githubTrello.service;

import java.util.Collection;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;
import com.sfeir.githubTrello.wrapper.Rest;

import static com.sfeir.githubTrello.domain.trello.List.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.lang.String.*;

public class TrelloService {

	public TrelloService(String token) {
		this.rest = new Rest(API_URL, format("&key=%s&token=%s", API_KEY, token));
	}

	public Card getCard(String cardId) {
		return fromJsonToObject(
				rest.url("/cards/%s", cardId).get(),
				Card.class);
	}

	public List getList(Board board, String listName) {
		for (List list : getListsFromBoard(board)) {
			if (listName.equals(list.getName())) {
				return list.withNewCardsInJson(getCardsFromList(list));
			}
		}
		return listBuilder().build();
	}

	private Collection<List> getListsFromBoard(Board board) {
		return fromJsonToObjects(
				rest.url("/boards/%s/lists", board.getId()).get(),
				List.class);
	}

	private String getCardsFromList(List list) {
		return rest.url("/lists/%s/cards", list.getId()).get();
	}

	protected Rest rest;

	private static final String API_URL = "https://api.trello.com/1";
	private static final String API_KEY = "d0e4aa36488c2e5957da7c3a61a76ff2";

}
