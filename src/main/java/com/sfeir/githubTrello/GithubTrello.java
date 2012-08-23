package com.sfeir.githubTrello;

import java.util.Collection;
import java.util.Map;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.List;

import static com.google.common.base.Objects.*;
import static com.google.common.base.Preconditions.*;
import static com.sfeir.githubTrello.BoardWatcher.*;
import static com.sfeir.githubTrello.domain.trello.List.*;

public class GithubTrello {

	public static void main(String[] args) {

		checkArgument(args.length == 4, "Missing arguments, expected size: 4");

		String token = args[0];
		String boardId = args[1];
		String startListName = args[2];
		String endListName = args[3];

		checkArgument(token.matches("\\w+"), "%s is not a valid token", token);
		checkArgument(boardId.matches("\\w+"), "%s is not a valid board identifier", boardId);
		checkArgument(startListName.matches("(\\w| )+"), "%s is not a valid list name", startListName);
		checkArgument(endListName.matches("(\\w| )+"), "%s is not a valid list name", endListName);

		Board board = new Board(boardId);

		TrelloService trelloService = new TrelloService(token);

		String startListId = trelloService.getListId(board, startListName);
		String endListId = trelloService.getListId(board, endListName);

		TrelloDatabase trelloDatabase = new TrelloDatabase(token, board);
		Map<String, List> oldListsByListId = trelloDatabase.fetchCardsByList();

		List oldStartList = firstNonNull(oldListsByListId.get(startListId), HOLLOW_LIST);
		List oldEndList = firstNonNull(oldListsByListId.get(endListId), HOLLOW_LIST);

		List newStartList = trelloService.getListWithCards(startListId);
		List newEndList = trelloService.getListWithCards(endListId);

		BoardWatcher backlogInProgressWatcher =
				boardWatcherBuilder().oldStartList(oldStartList).oldEndList(oldEndList).newStartList(newStartList).newEndList(newEndList).build();

		Collection<String> movedCards = backlogInProgressWatcher.getMovedCards();

		doStuff(movedCards);

		trelloDatabase.saveList(newStartList);
		trelloDatabase.saveList(newEndList);

		trelloDatabase.close();
	}

	private static void doStuff(Collection<String> movedCards) {
		System.out.println(movedCards);
	}
}
