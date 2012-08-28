package com.sfeir.githubTrello.domain.trello;

import java.util.Collection;

import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.util.Collections.*;
import static org.apache.commons.lang3.StringUtils.*;

public class List {

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getIdBoard() {
		return idBoard;
	}

	public boolean isClosed() {
		return closed;
	}

	public int getPos() {
		return pos;
	}

	public Collection<Card> getCards() {
		if (cards.isEmpty() && isNotEmpty(cardsInJson)) {
			cards = fromJsonToObjects(cardsInJson, Card.class);
		}
		return cards;
	}

	public String getCardsInJson() {
		return cardsInJson;
	}

	public static Builder listBuilder() {
		return new List.Builder();
	}

	public static class Builder {
		private String id;
		private String name;
		private boolean closed;
		private String idBoard;
		private int pos;
		private String cardsInJson;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder closed(boolean closed) {
			this.closed = closed;
			return this;
		}

		public Builder idBoard(String idBoard) {
			this.idBoard = idBoard;
			return this;
		}

		public Builder pos(int pos) {
			this.pos = pos;
			return this;
		}

		public Builder cardsInJson(String cardsInJson) {
			this.cardsInJson = cardsInJson;
			return this;
		}

		public List build() {
			List list = new List();
			list.cardsInJson = cardsInJson;
			list.id = id;
			list.name = name;
			list.closed = closed;
			list.idBoard = idBoard;
			list.pos = pos;
			return list;
		}
	}

	private String id;
	private String name;
	private boolean closed;
	private String idBoard;
	private int pos;
	private Collection<Card> cards = emptyList();
	private String cardsInJson;
}
