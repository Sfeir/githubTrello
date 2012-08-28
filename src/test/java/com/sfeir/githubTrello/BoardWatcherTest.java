package com.sfeir.githubTrello;

import java.util.Collection;

import org.junit.Test;

import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;

import static com.sfeir.githubTrello.BoardWatcher.*;
import static com.sfeir.githubTrello.domain.trello.Card.*;
import static com.sfeir.githubTrello.domain.trello.List.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.util.Arrays.*;
import static org.fest.assertions.Assertions.*;

public class BoardWatcherTest {


	@Test
	public void should_detect_that_two_cards_were_moved_between_watched_lists() {
		BoardWatcher boardWatcher = boardWatcherBuilder()
				.oldStartList(list("l01", card("c01"), card("c02"), card("c03")))
				.newStartList(list("l01", card("c01")))
				.oldEndList(list("l02", card("c04")))
				.newEndList(list("l02", card("c02"), card("c03"), card("c04")))
				.build();

		Collection<String> movedCards = boardWatcher.getMovedCards();
		assertThat(movedCards).containsOnly("c02", "c03");
	}


	@Test
	public void should_detect_that_no_card_was_moved_between_watched_lists() {
		BoardWatcher boardWatcher = boardWatcherBuilder()
				.oldStartList(list("l01", card("c01")))
				.newStartList(list("l01", card("c01"), card("c03")))
				.oldEndList(list("l02", card("c02"), card("c04")))
				.newEndList(list("l02", card("c02")))
				.build();

		Collection<String> movedCards = boardWatcher.getMovedCards();
		assertThat(movedCards).isEmpty();
	}

	private static List list(String listId, Card... cards) {
		return listBuilder().id(listId)
				.cardsInJson(fromType(asList(cards)).toJson()).build();
	}

	private static Card card(String idCard)
	{
		return cardBuilder().id(idCard).build();
	}

}
