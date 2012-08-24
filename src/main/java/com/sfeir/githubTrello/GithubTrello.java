package com.sfeir.githubTrello;

import java.sql.SQLException;
import java.util.Collection;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.List;

import static com.google.common.base.Preconditions.*;
import static com.sfeir.githubTrello.BoardWatcher.*;
import static com.sfeir.githubTrello.TrelloDatabase.*;
import static com.sfeir.githubTrello.domain.trello.List.*;

public class GithubTrello {

	public static void main(String[] args) {

		checkArgument(args.length == 4, "Missing some arguments out of the 4 expected");

		String trelloToken = args[0];
		String boardId = args[1];
		String startListName = args[2];
		String endListName = args[3];
		//		String githubToken = args[4];

		checkArgument(trelloToken.matches("\\w+"), "%s is not a valid token", trelloToken);
		checkArgument(boardId.matches("\\w+"), "%s is not a valid board identifier", boardId);
		checkArgument(startListName.matches("(\\w| )+"), "%s is not a valid list name", startListName);
		checkArgument(endListName.matches("(\\w| )+"), "%s is not a valid list name", endListName);

		Board board = new Board(boardId);

		TrelloService trelloService = new TrelloService(trelloToken);

		String startListId = trelloService.getListId(board, startListName);
		String endListId = trelloService.getListId(board, endListName);

		List newStartList = trelloService.getListWithCards(startListId);
		List newEndList = trelloService.getListWithCards(endListId);

		List oldStartList = HOLLOW_LIST;
		List oldEndList = HOLLOW_LIST;

		try (TrelloDatabase database = createTrelloDatabase(trelloToken, board, "", "src/main/resources/snapshots.csv")) {
			oldStartList = database.getList(startListId);
			oldEndList = database.getList(endListId);
			database.saveList(newStartList);
			database.saveList(newEndList);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		BoardWatcher toDoDoingWatcher = boardWatcherBuilder()
				.oldStartList(oldStartList)
				.newStartList(newStartList)
				.oldEndList(oldEndList)
				.newEndList(newEndList)
				.build();

		Collection<String> movedCards = toDoDoingWatcher.getMovedCards();

		System.out.println(movedCards);
	}
}
