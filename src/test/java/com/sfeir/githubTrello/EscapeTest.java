package com.sfeir.githubTrello;

import org.junit.Test;

import static com.sfeir.githubTrello.wrapper.Escape.*;
import static org.fest.assertions.Assertions.*;


public class EscapeTest {

	@Test
	public void should_escape_word() {
		assertThat(escape("@#${}[]")).isEmpty();
		assertThat(escape("àéîòü")).isEqualTo("aeiou");
		assertThat(escape("Hello World!")).isEqualTo("hello-world");
		assertThat(escape("-_-")).isEqualTo("-_-");
		assertThat(escape("42")).isEqualTo("42");
	}
}
