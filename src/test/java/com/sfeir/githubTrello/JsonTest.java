package com.sfeir.githubTrello;

import org.junit.Test;

import com.sfeir.githubTrello.domain.trello.Card;

import static com.google.common.collect.Collections2.*;
import static com.sfeir.githubTrello.Json.*;
import static com.sfeir.githubTrello.domain.trello.Card.*;
import static java.util.Arrays.*;
import static org.fest.assertions.Assertions.*;

public class JsonTest {

	@Test
	public void should_serialize_and_deserialize_correctly()
	{
		assertThat(
				transform(
						fromJson(
								fromType(
										asList(card("c01"), card("c02")))
										.toJson())
								.to(Card.class)
						, INTO_CARD_ID))
				.containsOnly(("c01"), ("c02"));
	}

	@Test
	public void should_deserialize_into_empty_collection() {
		assertThat(fromJson("[]").to(Card.class)).isEmpty();
	}

	private static Card card(String idCard)//TODO: Duplicated
	{
		return cardBuilder().id(idCard).build();
	}
}
