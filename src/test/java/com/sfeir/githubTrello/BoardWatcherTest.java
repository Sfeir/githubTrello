package com.sfeir.githubTrello;

import static com.google.common.collect.Lists.newArrayList;
import static com.sfeir.githubTrello.domain.trello.Board.boardBuilder;
import static com.sfeir.githubTrello.domain.trello.Card.cardBuilder;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableSet;
import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.Card;

public class BoardWatcherTest {

	@Test
	public void should_detect_that_one_card_was_moved() {

		Board incompleteBoard = spy(boardBuilder().initialListId("l01").finalListId("l02").build());

		doReturn(newArrayList(card("c01", "l01"), card("c02", "l01"), card("c03", "l02"))).when(incompleteBoard).getPreviousCards();
		doReturn(newArrayList(card("c01", "l01"), card("c02", "l02"), card("c03", "l02"))).when(incompleteBoard).getCurrentCards();

		assertThat(new BoardWatcher(incompleteBoard).getMovedCards()).isEqualTo(ImmutableSet.<String> of("c02"));
	}

	private Card card(String idCard, String idList) {
		return cardBuilder().id(idCard).idList(idList).build();
	}

}
