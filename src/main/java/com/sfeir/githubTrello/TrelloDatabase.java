package com.sfeir.githubTrello;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.List;

import static com.google.common.base.Preconditions.*;
import static com.google.common.base.Strings.*;
import static com.sfeir.githubTrello.domain.trello.List.*;
import static java.lang.String.*;
import static org.apache.commons.dbutils.DbUtils.*;

public final class TrelloDatabase implements AutoCloseable {

	public static TrelloDatabase createTrelloDatabase(String token, Board board, String name, String csvFile) throws SQLException {
		checkArgument(new File(csvFile).isFile(), "File %s not found", csvFile);

		TrelloDatabase trelloDatabase = new TrelloDatabase(token, board, csvFile);
		trelloDatabase.init(name);
		return trelloDatabase;
	}

	private void init(String name) throws SQLException {
		String createTable = format(
				"CREATE TABLE %s (%s INT PRIMARY KEY auto_increment, %s varchar(255), %s varchar(255), %s varchar(255), %s LONGVARCHAR) as SELECT * FROM CSVREAD('%s')",
				TABLE_NAME, SNAPSHOT_ID_FIELD, TOKEN_FIELD, BOARD_ID_FIELD, LIST_ID_FIELD, CARDS_FIELD, csvFile);

		String url = "jdbc:h2:mem:" + nullToEmpty(name);
		connection = DriverManager.getConnection(url, "sa", "");
		connection.setAutoCommit(true);
		try (Statement createStatement = connection.createStatement()) {
			createStatement.execute(createTable);
		}
	}

	public List getList(String listId) throws SQLException {
		String select =
				format("SELECT * FROM %s where %s=? and %s=? and %s=?",
						TABLE_NAME, TOKEN_FIELD, BOARD_ID_FIELD, LIST_ID_FIELD);
		try (PreparedStatement selectStatement = connection.prepareStatement(select)) {
			selectStatement.setString(1, token);
			selectStatement.setString(2, board.getId());
			selectStatement.setString(3, listId);
			ResultSet results = selectStatement.executeQuery();
			if (!results.next())
				return listBuilder().build();
			return listBuilder().id(results.getString(LIST_ID_FIELD)).cardsInJson(results.getString(CARDS_FIELD)).build();
		}
	}

	public void saveList(List list) throws SQLException
	{
		String merge = format("MERGE INTO %s (%s, %s, %s, %s) KEY(%s, %s, %s) VALUES (?, ?, ?, ?)",
				TABLE_NAME,
				TOKEN_FIELD, BOARD_ID_FIELD, LIST_ID_FIELD, CARDS_FIELD,
				TOKEN_FIELD, BOARD_ID_FIELD, LIST_ID_FIELD);
		try (PreparedStatement mergeStatement = connection.prepareStatement(merge)) {
			mergeStatement.setString(1, token);
			mergeStatement.setString(2, board.getId());
			mergeStatement.setString(3, list.getId());
			mergeStatement.setString(4, list.getCardsInJson());
			mergeStatement.execute();
		}
	}

	@Override
	public void close() throws SQLException {
		connection.createStatement().execute(format("CALL CSVWRITE('%s', 'SELECT * FROM %s')", csvFile, TABLE_NAME));
		closeQuietly(connection);
	}

	private TrelloDatabase(String token, Board board, String csvFile) {
		this.token = token;
		this.board = board;
		this.csvFile = csvFile;
	}

	private String token;
	private Board board;
	private Connection connection;
	private String csvFile;

	private static final String TABLE_NAME = "Trello_Snapshot";
	private static final String SNAPSHOT_ID_FIELD = "snapshot_id";
	private static final String TOKEN_FIELD = "token";
	private static final String BOARD_ID_FIELD = "board_id";
	private static final String LIST_ID_FIELD = "list_id";
	private static final String CARDS_FIELD = "cards";
}