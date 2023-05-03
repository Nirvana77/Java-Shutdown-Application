package me.navanda.shutdown_application.Services;

import java.sql.*;

@SuppressWarnings("unused")
public class Database {
	public enum LogLevel {
		ERROR,
		LOG,
		WARNING
	}

	private final Connection conn;

	public Database(String url, String port, String databaseName, String username, String password) throws SQLException, ClassNotFoundException {
		password = "Ysos$Gn8JZ"; // TODO: Replace with secure password storage mechanism
		conn = createConnection(url, port, databaseName, username, password);
		conn.setAutoCommit(false);  // start transaction
	}

	private Connection createConnection(String url, String port, String databaseName, String username, String password) throws SQLException, ClassNotFoundException {
		// Set up the database connection
		Class.forName("org.postgresql.Driver"); // load the driver class into memory
		return DriverManager.getConnection("jdbc:postgresql://" + url + ":" + port + "/" + databaseName, username, password.equals("") ? null : password);
	}

	public boolean tableExists(String tableName) throws SQLException {
		boolean exists = false;
		DatabaseMetaData meta = conn.getMetaData();
		ResultSet tables = meta.getTables(null, null, tableName, new String[]{"TABLE"});
		while (tables.next()) {
			String name = tables.getString("TABLE_NAME");
			if (name != null && name.equals(tableName)) {
				exists = true;
				break;
			}
		}
		return exists;
	}

	public void log(LogLevel logLevel, String message) throws SQLException {
		String sql =
				"INSERT INTO logs (" +
						"log_time, " +
						"log_level, " +
						"message" +
						")\n" +
						"VALUES (?, ?, ?);\n";

		executeSQL(sql, new Timestamp(System.currentTimeMillis()), logLevel.name(), message);
	}

	public Connection getConn() {
		return conn;
	}

	public Statement createStatement() throws SQLException {
		return conn.createStatement();
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}

	public void close() throws SQLException {
		conn.close();
	}

	public ResultSet executeSQLWithReturn(String sql, Object... args) throws SQLException {
		ResultSet result = null;
		PreparedStatement stmt = null;
		try {
			stmt = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			result = stmt.executeQuery();
			if (!result.next()) {
				result = null;
			}
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				System.getLogger("Failed to rollback transaction: " + ex.getMessage());
				return null;
			}
			System.getLogger("Failed to execute SQL query: " + e.getMessage());
			throw e;
		}
		return result;
	}

	public void executeSQL(String sql, Object... args) throws SQLException {
		try {
			PreparedStatement stmt = conn.prepareStatement(sql);
			for (int i = 0; i < args.length; i++) {
				stmt.setObject(i + 1, args[i]);
			}
			stmt.executeUpdate();
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				System.getLogger("Failed to rollback transaction: " + ex.getMessage());
				return;
			}
			System.getLogger("Failed to execute SQL query: " + e.getMessage());
			throw e;
		}
	}

}
