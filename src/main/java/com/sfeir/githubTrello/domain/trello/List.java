package com.sfeir.githubTrello.domain.trello;

import java.util.Collection;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.util.Collections.*;
import static org.apache.commons.lang3.StringUtils.*;

@JsonIgnoreProperties(ignoreUnknown = true)
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

	public Collection<Card> getCards() {
		if (cards.isEmpty() && isNotEmpty(cardsInJson)) {
			cards = fromJsonToObjects(cardsInJson, Card.class);
		}
		return cards;
	}

	public String getCardsInJson() {
		return cardsInJson;//TODO: Convert from json outside?
	}

	public List withNewCardsInJson(String newCardsInJson) {
		return listBuilder()
				.id(this.id)
				.name(this.name)
				.idBoard(this.idBoard)
				.cardsInJson(newCardsInJson)
				.build();
	}

	public static Builder listBuilder() {
		return new List.Builder();
	}

	public static class Builder {

		public Builder id(String id) {
			this.id = id;
			return this;
		}

		public Builder name(String name) {
			this.name = name;
			return this;
		}

		public Builder idBoard(String idBoard) {
			this.idBoard = idBoard;
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
			list.idBoard = idBoard;
			return list;
		}

		private String id;
		private String name;
		private String idBoard;
		private String cardsInJson;
	}

	private String id;
	private String name;
	private String idBoard;
	private Collection<Card> cards = emptyList();
	private String cardsInJson;
}
