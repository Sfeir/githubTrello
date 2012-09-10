package com.sfeir.githubTrello.domain.trello;

import java.util.Collection;

import org.junit.Test;

import com.sfeir.githubTrello.domain.trello.BoardWatcher;

import static com.sfeir.githubTrello.Tools.*;
import static com.sfeir.githubTrello.domain.trello.BoardWatcher.*;
import static org.fest.assertions.Assertions.*;

public class BoardWatcherTest {


	@Test
	public void should_detect_that_two_cards_were_moved_between_watched_lists() {
		BoardWatcher boardWatcher = boardWatcherBuilder()
				.oldStartList(list("l01", "L1", card("c01"), card("c02"), card("c03")))
				.newStartList(list("l01", "L1", card("c01")))
				.oldEndList(list("l02", "L2", card("c04")))
				.newEndList(list("l02", "L2", card("c02"), card("c03"), card("c04")))
				.build();

		Collection<String> movedCards = boardWatcher.getMovedCards();
		assertThat(movedCards).containsOnly("c02", "c03");
	}


	@Test
	public void should_detect_that_no_card_was_moved_between_watched_lists() {
		BoardWatcher boardWatcher = boardWatcherBuilder()
				.oldStartList(list("l01", "L1", card("c01")))
				.newStartList(list("l01", "L1", card("c01"), card("c03")))
				.oldEndList(list("l02", "L2", card("c02"), card("c04")))
				.newEndList(list("l02", "L2", card("c02")))
				.build();

		Collection<String> movedCards = boardWatcher.getMovedCards();
		assertThat(movedCards).isEmpty();
	}


}
