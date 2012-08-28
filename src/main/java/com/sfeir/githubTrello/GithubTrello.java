package com.sfeir.githubTrello;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;

import static com.google.common.base.Preconditions.*;
import static com.sfeir.githubTrello.BoardWatcher.*;
import static com.sfeir.githubTrello.GithubService.*;
import static com.sfeir.githubTrello.TrelloDatabase.*;
import static java.lang.String.*;

public final class GithubTrello {

	public static void main(String[] args) {

		String trelloToken = get("trello.token");
		String trelloCsvDatabasePath = get("trello.csv.database");
		String trelloBoardId = get("trello.board-id");
		String trelloToDoListName = get("trello.to-do-list.name");
		String trelloDoingListName = get("trello.doing-list.name");
		String githubToken = get("github.token");
		String githubUser = get("github.user");
		String githubRepository = get("github.repo");
		String githubDevelopBranch = get("github.develop-branch");

		Board board = new Board(trelloBoardId);

		TrelloService trelloService = new TrelloService(trelloToken);

		String toDoListId = trelloService.getListId(board, trelloToDoListName);
		String doingListId = trelloService.getListId(board, trelloDoingListName);

		List newToDoList = trelloService.getListWithCards(toDoListId);
		List newDoingList = trelloService.getListWithCards(doingListId);

		List oldToDoList = null;
		List oldDoingList = null;

		try (TrelloDatabase database = trelloDatabaseBuilder()
				.board(board)
				.csvFileName(trelloCsvDatabasePath)
				.token(trelloToken)
				.build()) {
			oldToDoList = database.getList(toDoListId);
			oldDoingList = database.getList(doingListId);
			database.saveList(newToDoList);
			database.saveList(newDoingList);
		}
		catch (SQLException | IOException e) {
			logger.error(e, e);
		}

		BoardWatcher toDoDoingWatcher = boardWatcherBuilder()
				.oldStartList(oldToDoList)
				.newStartList(newToDoList)
				.oldEndList(oldDoingList)
				.newEndList(newDoingList)
				.build();

		GithubService githubService = githubServiceBuilder()
				.token(githubToken)
				.user(githubUser)
				.repository(githubRepository)
				.baseBranch(githubDevelopBranch)
				.build();

		for (String cardId : toDoDoingWatcher.getMovedCards()) {
			Card card = trelloService.getCard(cardId);
			githubService.createFeatureBranch(format("%s_%s", card.getName(), cardId));
		}
	}

	private static String get(String property) {
		return checkNotNull(System.getProperty(property), "Missing property " + property);
	}

	private GithubTrello() {}

	private static final Log logger = LogFactory.getLog(GithubTrello.class);
}
