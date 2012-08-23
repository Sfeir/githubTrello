package com.sfeir.githubTrello;

import java.util.Collection;

import org.junit.Test;

import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;

import static com.google.common.collect.Collections2.*;
import static com.sfeir.githubTrello.BoardWatcher.*;
import static com.sfeir.githubTrello.Json.*;
import static com.sfeir.githubTrello.domain.trello.Card.*;
import static com.sfeir.githubTrello.domain.trello.List.*;
import static java.util.Arrays.*;
import static org.fest.assertions.Assertions.*;

public class BoardWatcherTest {

	@Test
	public void should_detect_that_one_card_was_moved() {

		BoardWatcher boardWatcher = boardWatcherBuilder()
				.oldStartList(list("l01", card("c01"), card("c02")))
				.oldEndList(list("l02", card("c03")))
				.newStartList(list("l01", card("c01")))
				.newEndList(list("l02", card("c02"), card("c03")))
				.build();

		Collection<String> movedCards = boardWatcher.getMovedCards();

		assertThat(movedCards).containsOnly("c02");
	}

	private static List list(String listId, Card... cards) {
		return listBuilder().id(listId)
				.cardsInJson(fromType(asList(cards)).toJson()).build();
	}

	private static Card card(String idCard)
	{
		return cardBuilder().id(idCard).build();
	}

	@Test
	public void should_serialize_correctly()
	{
		assertThat(
				transform(
						fromJson(
								fromType(asList(card("c01"), card("c02"))).toJson())
								.to(Card.class)
						, INTO_CARD_ID))
				.containsOnly(("c01"), ("c02"));
	}

	@Test
	public void should_deserialize_into_empty_collection() {
		assertThat(fromJson("[]").to(Card.class)).isEmpty();
	}
}
