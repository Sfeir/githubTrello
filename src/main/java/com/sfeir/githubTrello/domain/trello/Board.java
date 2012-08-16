package com.sfeir.githubTrello.domain.trello;

import static java.util.Collections.emptyList;

import java.util.List;

public class Board {// Unsure of the name

	private static String apiKey = "d0e4aa36488c2e5957da7c3a61a76ff2";

	private String token;
	private String boardId;
	private String initialListId;
	private String finalListId;

	public List<Card> getPreviousCards() {
		return emptyList();
	}

	public List<Card> getCurrentCards() {
		return emptyList();
	}
	
	public String getInitialListId() {
		return initialListId;
	}
	
	public String getFinalListId() {
		return finalListId;
	}

	public static Builder boardBuilder() {
		return new Board.Builder();
	}

	public static class Builder {
		private String token;
		private String boardId;
		private String initialListId;
		private String finalListId;

		public Builder token(String token) {
			this.token = token;
			return this;
		}

		public Builder boardId(String boardId) {
			this.boardId = boardId;
			return this;
		}

		public Builder initialListId(String initialListId) {
			this.initialListId = initialListId;
			return this;
		}

		public Builder finalListId(String finalListId) {
			this.finalListId = finalListId;
			return this;
		}

		public Board build() {
			Board board = new Board();
			board.token = token;
			board.boardId = boardId;
			board.initialListId = initialListId;
			board.finalListId = finalListId;
			return board;
		}
	}
}
