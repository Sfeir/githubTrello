package com.sfeir.githubTrello.service;

import java.util.Collection;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;
import com.sfeir.githubTrello.wrapper.RestClient;

import static com.sfeir.githubTrello.domain.trello.Card.*;
import static com.sfeir.githubTrello.domain.trello.List.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.lang.String.*;

public class TrelloService {

	public TrelloService(String token) {
		this.restClient = new RestClient(API_URL, format("&key=%s&token=%s", API_KEY, token));
	}

	public final Card getCard(String cardId) {
		return fromJsonToObject(
				restClient.url("/cards/%s", cardId).get(),
				Card.class);
	}

	public final List getList(Board board, String listName) {
		for (List list : getListsFromBoard(board)) {
			if (listName.equals(list.getName())) {
				return list.withNewCardsInJson(getCardsFromList(list));
			}
		}
		return listBuilder().build();
	}

	public final Card updateCardDescription(Card card, String newDescription) {
		return cardBuilder().build();
	}

	private Collection<List> getListsFromBoard(Board board) {
		return fromJsonToObjects(
				restClient.url("/boards/%s/lists", board.getId()).get(),
				List.class);
	}

	private String getCardsFromList(List list) {
		return restClient.url("/lists/%s/cards", list.getId()).get();
	}

	protected RestClient restClient;

	private static final String API_URL = "https://api.trello.com/1";
	private static final String API_KEY = "d0e4aa36488c2e5957da7c3a61a76ff2";

}
