package com.sfeir.githubTrello.domain.trello;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.google.common.base.Objects;

import static com.google.common.base.Objects.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {

	public String getId() {
		return this.id;
	}

	public String getIdBoard() {
		return this.idBoard;
	}

	public String getIdList() {
		return this.idList;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("idBoard", idBoard).add("idList", idList).add("name", name).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, idBoard, idList, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (obj instanceof Card) {
			Card other = (Card) obj;
			return equal(id, other.id) && equal(idBoard, other.idBoard) && equal(idList, other.idList) && equal(name, other.name);
		}
		return false;
	}

	public static Builder cardBuilder() {
		return new Card.Builder();
	}


	public static class Builder {
		private String id;
		private String idBoard;
		private String idList;
		private String name;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder idBoard(String idBoard) {
			this.idBoard = idBoard;
			return this;
		}

		public Builder idList(String idList) {
			this.idList = idList;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Card build() {
			Card card = new Card();
			card.id = id;
			card.idBoard = idBoard;
			card.idList = idList;
			card.name = name;
			return card;
		}
	}


	private String id;
	private String idBoard;
	private String idList;
	private String name;

}
