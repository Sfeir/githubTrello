package com.sfeir.githubTrello;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.List;

import static com.google.common.base.Preconditions.*;

import static com.google.common.collect.Maps.*;

import static com.sfeir.githubTrello.domain.trello.List.*;
import static java.lang.String.*;
import static java.util.Collections.*;

//TODO: Add commons DBUtils
public class TrelloDatabase {

	private static final String CSV_PATH = "src/main/resources/snapshots.csv";
	private static final String TABLE_NAME = "Trello_Snapshot";
	private static final String SNAPSHOT_ID_FIELD = "snapshot_id";
	private static final String TOKEN_FIELD = "token";
	private static final String BOARD_ID_FIELD = "board_id";
	private static final String LIST_ID_FIELD = "list_id";
	private static final String CARDS_FIELD = "cards";
	private static Connection connection;

	static {
		String url = "jdbc:h2:mem:";
		String createTable = format(
				"CREATE TABLE %s (%s INT PRIMARY KEY auto_increment, %s varchar(255), %s varchar(255), %s varchar(255), %s LONGVARCHAR) as SELECT * FROM CSVREAD('%s')",
				TABLE_NAME, SNAPSHOT_ID_FIELD, TOKEN_FIELD, BOARD_ID_FIELD, LIST_ID_FIELD, CARDS_FIELD, CSV_PATH);

		try {
			connection = DriverManager.getConnection(url, "sa", "");
			connection.setAutoCommit(true);
			connection.createStatement().execute(createTable);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private String token;
	private Board board;


	public TrelloDatabase(String token, Board board) {
		checkArgument(connection != null);
		this.token = token;
		this.board = board;
	}

	public Map<String, List> fetchCardsByList() {
		//		conn.createStatement().executeUpdate("insert into Trello_Snapshot (TOKEN,BOARD_ID,LIST_ID,CARDS) values(17,18,19,20)");
		try {
			Map<String, List> listsWithCards = newHashMap();
			String select = format("SELECT * FROM %s where %s=? and %s=?", TABLE_NAME, TOKEN_FIELD, BOARD_ID_FIELD);
			PreparedStatement selectStatement = connection.prepareStatement(select);
			selectStatement.setString(1, token);
			selectStatement.setString(2, board.getBoardId());
			ResultSet results = selectStatement.executeQuery();
			while (results.next()) {
				String listId = results.getString(LIST_ID_FIELD);
				String cards = results.getString(CARDS_FIELD);
				listsWithCards.put(listId, listBuilder().id(listId).cardsInJson(cards).build());
			}
			return listsWithCards;
		}
		catch (SQLException e) {
			//			logger.error(e, e);
			e.printStackTrace();
			return emptyMap();
		}
	}

	public void saveList(List list)
	{
		try {
			String merge = format("MERGE INTO %s (%s, %s, %s, %s) KEY(%s, %s, %s) VALUES (?, ?, ?, ?)",
					TABLE_NAME,
					TOKEN_FIELD, BOARD_ID_FIELD, LIST_ID_FIELD, CARDS_FIELD,
					TOKEN_FIELD, BOARD_ID_FIELD, LIST_ID_FIELD);
			PreparedStatement mergeStatement = connection.prepareStatement(merge);
			mergeStatement.setString(1, token);
			mergeStatement.setString(2, board.getBoardId());
			mergeStatement.setString(3, list.getId());
			mergeStatement.setString(4, list.getCardsInJson());
			mergeStatement.executeUpdate();
		}
		catch (SQLException e) {
			//			logger.error(e, e);
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			connection.createStatement().executeQuery(
					format("CALL CSVWRITE('%s', 'SELECT * FROM %s')",
							CSV_PATH, TABLE_NAME)
					);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		if (connection != null)
			try {
				connection.close();
			}
			catch (SQLException e) {
				//logger.error(e, e);FIXME
			e.printStackTrace();
		}
	}

}
