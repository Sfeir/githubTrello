package com.sfeir.githubTrello;

import org.junit.Test;

import com.sfeir.githubTrello.domain.trello.Card;

import static com.sfeir.githubTrello.domain.trello.Card.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.util.Arrays.*;
import static org.fest.assertions.Assertions.*;

public class JsonTest {

	@Test
	public void should_deserialize_serialized_object_correctly() {
		assertThat(
				fromJson(
						fromType(asList(card("c01"), card("c02"))).toJson())
						.toCollection(Card.class))
				.containsOnly(card("c01"), card("c02"));
	}

	@Test
	public void should_deserialize_into_empty_collection() {
		assertThat(fromJson("[]").toCollection(Card.class)).isEmpty();
	}

	@Test
	public void should_deserialize_into_empty_card() {
		assertThat(fromJson("").toObject(Card.class)).isEqualTo(card(null));
	}

	private static Card card(String idCard) {
		return cardBuilder().id(idCard).build();
	}
}
