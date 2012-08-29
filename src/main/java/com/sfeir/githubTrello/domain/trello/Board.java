package com.sfeir.githubTrello.domain.trello;


public class Board {

	public Board(String boardId) {
		this.boardId = boardId;
	}

	public String getId() {
		return boardId;
	}

	private String boardId;

}
