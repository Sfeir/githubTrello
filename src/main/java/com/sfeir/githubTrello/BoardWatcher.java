package com.sfeir.githubTrello;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.Card;

public class BoardWatcher {

	public BoardWatcher(Board board) {
		this.board = board;
	}

	public Set<String> getMovedCards() {
		Set<String> initialCardIds = newHashSet();
		Set<String> finalCardIds = newHashSet();
		
		for (Card card : board.getPreviousCards()) {
			if (board.getInitialListId().equals(card.getIdList()))
				initialCardIds.add(card.getId());
		}
		
		for (Card card : board.getCurrentCards()) {
			if (board.getFinalListId().equals(card.getIdList()))
				finalCardIds.add(card.getId());
		}
		
		return Sets.intersection(initialCardIds, finalCardIds).immutableCopy();
	}

	private Board board;

}
