package com.sfeir.githubTrello;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.sfeir.githubTrello.domain.trello.Board;
import com.sfeir.githubTrello.domain.trello.List;

import static com.google.common.base.Strings.*;
import static com.sfeir.githubTrello.domain.trello.List.*;
import static java.lang.String.*;
import static org.apache.commons.dbutils.DbUtils.*;

public final class TrelloDatabase implements AutoCloseable {



	private void init(String name) throws SQLException, IOException {
		String table = format("CREATE TABLE %s (%s INT PRIMARY KEY auto_increment, %s varchar(255), %s varchar(255), %s varchar(255), %s LONGVARCHAR)",
				TABLE_NAME, SNAPSHOT_ID_FIELD, TOKEN_FIELD, BOARD_ID_FIELD, LIST_ID_FIELD, CARDS_FIELD);

		File csvFile = new File(csvFileName);
		if (csvFile.exists() && csvFile.isFile()) {
			table += format(" as SELECT * FROM CSVREAD('%s')", csvFileName);
		}
		else {
			if (!csvFile.createNewFile()) {
				throw new IOException("Can't create file " + csvFileName);
			}
		}

		String url = "jdbc:h2:mem:" + nullToEmpty(name);
		connection = DriverManager.getConnection(url, "sa", "");
		connection.setAutoCommit(true);
		try (Statement createStatement = connection.createStatement()) {
			createStatement.execute(table);
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
			if (!results.next()) {
				return listBuilder().build();
			}
			return listBuilder().id(results.getString(LIST_ID_FIELD)).cardsInJson(results.getString(CARDS_FIELD)).build();
		}
	}

	public void saveList(List list) throws SQLException {
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
		try {
			connection.createStatement().execute(format("CALL CSVWRITE('%s', 'SELECT * FROM %s')", csvFileName, TABLE_NAME));
		}
		finally {
			closeQuietly(connection);
		}
	}

	public static Builder trelloDatabaseBuilder() {
		return new TrelloDatabase.Builder();
	}

	public static class Builder {
		private String token;
		private Board board;
		private String csvFileName;
		private String databaseName;

		public Builder token(String token) {
			this.token = token;
			return this;
		}

		public Builder board(Board board) {
			this.board = board;
			return this;
		}

		public Builder csvFileName(String csvFileName) {
			this.csvFileName = csvFileName;
			return this;
		}

		public Builder databaseName(String databaseName) {
			this.databaseName = databaseName;
			return this;
		}

		public TrelloDatabase build() throws SQLException, IOException {
			TrelloDatabase trelloDatabase = new TrelloDatabase();
			trelloDatabase.token = token;
			trelloDatabase.board = board;
			trelloDatabase.csvFileName = csvFileName;
			trelloDatabase.init(databaseName);
			return trelloDatabase;
		}
	}

	private String token;
	private Board board;
	private Connection connection;
	private String csvFileName;

	private static final String TABLE_NAME = "Trello_Snapshot";
	private static final String SNAPSHOT_ID_FIELD = "snapshot_id";
	private static final String TOKEN_FIELD = "token";
	private static final String BOARD_ID_FIELD = "board_id";
	private static final String LIST_ID_FIELD = "list_id";
	private static final String CARDS_FIELD = "cards";

}