package com.sfeir.githubTrello.wrapper;

import java.text.Normalizer;

public class Escape {

	public static String escape(String word) {
		return stripAccents(word)
				.replaceAll("\\s+", "-")
				.replaceAll("([^\\w-])", "")
				.toLowerCase();
	}

	private static String stripAccents(String word) {
		return Normalizer.normalize(word, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
	}

	private Escape() {}

}
