package com.sfeir.githubTrello;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;

import static com.sfeir.githubTrello.BoardWatcher.*;
import static com.sfeir.githubTrello.GithubService.*;
import static com.sfeir.githubTrello.TrelloDatabase.*;
import static java.lang.System.*;

public final class GithubTrello {

	public static void main(String[] args) {

		String trelloToken = getProperty("trello.token");
		String trelloBoardId = getProperty("trello.board-id");
		String trelloToDoListName = getProperty("trello.to-do-list.name");
		String trelloDoingListName = getProperty("trello.doing-list.list.name");
		String githubToken = getProperty("github.token");
		String githubUser = getProperty("github.user");
		String githubRepository = getProperty("github.repo");
		String githubDevelopBranch = getProperty("github.develop-branch");

		Board board = new Board(trelloBoardId);

		TrelloService trelloService = new TrelloService(trelloToken);

		String startListId = trelloService.getListId(board, trelloToDoListName);
		String endListId = trelloService.getListId(board, trelloDoingListName);

		List newStartList = trelloService.getListWithCards(startListId);
		List newEndList = trelloService.getListWithCards(endListId);

		List oldStartList = null;
		List oldEndList = null;

		try (TrelloDatabase database = createTrelloDatabase(trelloToken, board, "", "src/main/resources/snapshots.csv")) {
			oldStartList = database.getList(startListId);
			oldEndList = database.getList(endListId);
			database.saveList(newStartList);
			database.saveList(newEndList);
		}
		catch (SQLException e) {
			logger.error(e, e);
		}

		BoardWatcher toDoDoingWatcher = boardWatcherBuilder()
				.oldStartList(oldStartList)
				.newStartList(newStartList)
				.oldEndList(oldEndList)
				.newEndList(newEndList)
				.build();

		GithubService githubService = githubServiceBuilder()
				.token(githubToken)
				.user(githubUser)
				.repository(githubRepository)
				.baseBranch(githubDevelopBranch)
				.build();

		for (String cardId : toDoDoingWatcher.getMovedCards()) {
			Card card = trelloService.getCard(cardId);
			githubService.createFeatureBranch(card.getName());
		}
	}

	private GithubTrello() {}

	private static final Log logger = LogFactory.getLog(GithubTrello.class);
}
