package com.sfeir.githubTrello;

import java.util.Collection;

import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;

import static com.google.common.collect.Collections2.*;

import static com.google.common.collect.Lists.*;

import static com.sfeir.githubTrello.domain.trello.Card.*;

public class BoardWatcher {

	public Collection<String> getMovedCards() {
		Collection<Card> startListCards = difference(oldStartList.getCards(), newStartList.getCards());
		Collection<Card> endListCards = difference(newEndList.getCards(), oldEndList.getCards());

		Collection<String> startListCardIds = transform(startListCards, INTO_CARD_ID);
		Collection<String> endListCardIds = transform(endListCards, INTO_CARD_ID);

		return intersection(endListCardIds, startListCardIds);
	}

	private List oldStartList;
	private List newStartList;
	private List oldEndList;
	private List newEndList;

	public static Builder boardWatcherBuilder() {//NOTE: Template
		BoardWatcher.Builder builder = new BoardWatcher.Builder();
		return builder;
	}

	private static <T> Collection<T> difference(Collection<T> left, Collection<T> right)
	{
		Collection<T> result = newArrayList(left);
		result.removeAll(newArrayList(right));
		return result;
	}

	private static <T> Collection<T> intersection(Collection<T> left, Collection<T> right)
	{
		Collection<T> result = newArrayList(left);
		result.retainAll(newArrayList(right));
		return result;
	}

	public static class Builder {
		private List oldStartList;
		private List newStartList;
		private List oldEndList;
		private List newEndList;

		public Builder oldStartList(List oldStartList) {
			this.oldStartList = oldStartList;
			return this;
		}

		public Builder newStartList(List newStartList) {
			this.newStartList = newStartList;
			return this;
		}

		public Builder oldEndList(List oldEndList) {
			this.oldEndList = oldEndList;
			return this;
		}

		public Builder newEndList(List newEndList) {
			this.newEndList = newEndList;
			return this;
		}


		public BoardWatcher build() {
			BoardWatcher boardWatcher = new BoardWatcher();
			boardWatcher.oldStartList = oldStartList;
			boardWatcher.oldEndList = oldEndList;
			boardWatcher.newStartList = newStartList;
			boardWatcher.newEndList = newEndList;
			return boardWatcher;
		}
	}
}
