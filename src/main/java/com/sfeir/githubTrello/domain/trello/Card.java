package com.sfeir.githubTrello.domain.trello;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.Objects;

import static com.google.common.base.Objects.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Card {

	public String getId() {
		return this.id;
	}

	public String getBoardId() {
		return this.boardId;
	}

	public String getListId() {
		return this.listId;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return toStringHelper(this).add("id", id).add("boardId", boardId).add("listId", listId).add("name", name).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id, boardId, listId, name);
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
			return equal(id, other.id) && equal(boardId, other.boardId) && equal(listId, other.listId) && equal(name, other.name);
		}
		return false;
	}


	public Card inNewList(List newList) {
		return cardBuilder().id(id).boardId(boardId).name(name).listId(newList.getId()).build();
	}

	public static Builder cardBuilder() {
		return new Card.Builder();
	}


	public static class Builder {
		private String id;
		private String boardId;
		private String listId;
		private String name;

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder boardId(String boardId) {
			this.boardId = boardId;
			return this;
		}

		public Builder listId(String listId) {
			this.listId = listId;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Card build() {
			Card card = new Card();
			card.id = id;
			card.boardId = boardId;
			card.listId = listId;
			card.name = name;
			return card;
		}
	}


	private String id;
	private String name;
	@JsonProperty("idBoard") private String boardId;
	@JsonProperty("idList") private String listId;

}
