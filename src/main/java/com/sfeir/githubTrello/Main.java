package com.sfeir.githubTrello;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import com.sfeir.githubTrello.domain.github.Branch;
import com.sfeir.githubTrello.domain.github.PullRequest;
import com.sfeir.githubTrello.domain.github.Repository;
import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.BoardWatcher;
import com.sfeir.githubTrello.domain.trello.Card;
import com.sfeir.githubTrello.domain.trello.List;
import com.sfeir.githubTrello.service.GithubService;
import com.sfeir.githubTrello.service.TrelloService;

import static com.google.common.base.Preconditions.*;
import static com.sfeir.githubTrello.TrelloDatabase.*;
import static com.sfeir.githubTrello.domain.github.Repository.*;
import static com.sfeir.githubTrello.domain.trello.BoardWatcher.*;
import static com.sfeir.githubTrello.domain.trello.List.*;
import static com.sfeir.githubTrello.wrapper.Escape.*;
import static java.lang.String.*;

public final class Main {

	public static void main(String[] args) throws IOException, SQLException {

		Properties properties = new Properties();
		properties.load(Main.class.getResourceAsStream(args[0]));

		String trelloToken = get("trello.token", properties);
		String trelloCsvDatabasePath = get("trello.csv.database", properties);
		String trelloBoardId = get("trello.board-id", properties);
		String trelloToDoListName = get("trello.to-do-list.name", properties);
		String trelloDoingListName = get("trello.doing-list.name", properties);
		String githubToken = get("github.token", properties);
		String githubUser = get("github.user", properties);
		String githubRepositoryName = get("github.repo", properties);
		String githubDevelopBranch = get("github.develop-branch", properties);

		Board board = new Board(trelloBoardId);

		TrelloService trelloService = new TrelloService(trelloToken);

		List newToDoList = trelloService.getList(board, trelloToDoListName);
		List newDoingList = trelloService.getList(board, trelloDoingListName);
		List oldToDoList = listBuilder().build();
		List oldDoingList = listBuilder().build();

		try (TrelloDatabase database = trelloDatabaseBuilder()
				.board(board)
				.csvFileName(trelloCsvDatabasePath)
				.token(trelloToken)
				.build()) {
			oldToDoList = database.getList(newToDoList.getId());
			oldDoingList = database.getList(newDoingList.getId());
			database.saveList(newToDoList);
			database.saveList(newDoingList);
		}

		BoardWatcher toDoDoingWatcher = boardWatcherBuilder()
				.oldStartList(oldToDoList)
				.oldEndList(oldDoingList)
				.newStartList(newToDoList)
				.newEndList(newDoingList)
				.build();

		Repository githubRepository = repositoryBuilder()
				.baseBranch(githubDevelopBranch)
				.user(githubUser)
				.name(githubRepositoryName)
				.build();

		GithubService githubService = new GithubService(githubRepository, githubToken);

		for (String cardId : toDoDoingWatcher.getMovedCards()) {
			Card card = trelloService.getCard(cardId);
			githubService.createFeatureBranch(asBranchName(card));
		}

		for (Card card : newDoingList.getCards()) {
			Branch featureBranch = githubService.getBranch(asBranchName(card));
			if (featureBranch.exists()
					&& githubService.hasCommitsOnBranch(featureBranch)
					&& githubService.hasNoPullRequestForBranch(featureBranch)) {
				PullRequest pullRequest = githubService.createPullRequest(card.getName(), card.getDescription(), featureBranch);
				githubService.updatePullRequestDescription(pullRequest, appendUrlToDescription(pullRequest.getDescription(), card.getUrl()));
				//trelloService.updateCardDescription(card, appendUrlToDescription(card.getDescription(), pullRequest.getHtmlUrl()));
			}
		}
	}

	private static String appendUrlToDescription(String description, String url) {
		return format("%s\n[%s](%<)", description, url);
	}

	private static String asBranchName(Card card) {
		return escape(format("%s_%s", card.getName(), card.getId()));
	}

	private static String get(String key, Properties properties) {
		return checkNotNull(properties.get(key), "Missing property " + key).toString();
	}

	private Main() {}
}
