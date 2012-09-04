package com.sfeir.githubTrello;

import org.junit.Ignore;

import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;

import static com.sfeir.githubTrello.domain.trello.Card.*;
import static com.sfeir.githubTrello.domain.trello.List.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.util.Arrays.*;

@Ignore
public class Tools {


	public static List list(String listId, String name, Card... cards) {
		return listBuilder().id(listId).name(name).cardsInJson(fromObjectToJson(asList(cards))).build();
	}

	public static Card card(String name, String idCard, String idList, String idBoard) {
		return cardBuilder().name(name).id(idCard).idList(idList).idBoard(idBoard).build();
	}

	public static Card card(String idCard) {
		return cardBuilder().id(idCard).build();
	}

}
