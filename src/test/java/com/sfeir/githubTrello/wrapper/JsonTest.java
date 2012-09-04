package com.sfeir.githubTrello.wrapper;

import org.junit.Test;

import com.sfeir.githubTrello.domain.trello.Card;

import static com.sfeir.githubTrello.Tools.*;
import static com.sfeir.githubTrello.wrapper.Json.*;
import static java.util.Arrays.*;
import static org.fest.assertions.Assertions.*;


public class JsonTest {

	@Test
	public void should_deserialize_serialized_object_correctly() {
		assertThat(
				fromJsonToObjects(
						fromObjectToJson(asList(card("c01"), card("c02"))),
						Card.class))
				.containsOnly(card("c01"), card("c02"));
	}

	@Test
	public void should_deserialize_into_empty_collection() {
		assertThat(fromJsonToObjects("[]", Card.class)).isEmpty();
	}

	@Test
	public void should_deserialize_into_empty_card() {
		assertThat(fromJsonToObject("", Card.class)).isEqualTo(card(null));
	}


}
