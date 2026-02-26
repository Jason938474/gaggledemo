package com.gaggledemo;

import com.gaggledemo.util.SQLUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.List;
import java.util.UUID;

class GaggledemoApplicationTests {
	protected static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	/**
	 * Runs startup.sql against throwaway instance to validate syntax
	 */
	@Test
	public void testStartupSequence() throws Exception {
		// we don't want persistence, we just use it to check syntax
		try (Connection conn = SQLUtil.getConn("throwaway", false)) {
			String schemaInput = Files.readString(Path.of(getClass().getResource("/sql/startup.sql").toURI()));
			List<String> statements = SQLUtil.parseSql(schemaInput);
			SQLUtil.runSqlList(conn, statements);
		}
	}

	/**
	 * This just ensures that the in memory database persists across closed connections
	 */
	@Test
	public void testInMemoryDbConnectionPersistence() throws SQLException {
		// Using random string for name just to ensure we aren't reading data from a previous run
		final String testData = UUID.randomUUID().toString();

		logger.info("Creating test schema...");

		// First connection - create table and insert data
		// Using a test DB name for this test to prevent overlap with other DBs
		try (Connection conn1 = SQLUtil.getConn("testDB", true)) {
			try (Statement stmt = conn1.createStatement()) {
				stmt.execute("CREATE TABLE test (id INT PRIMARY KEY AUTO_INCREMENT, name VARCHAR(999) NOT NULL)");

				PreparedStatement ps = conn1.prepareStatement("INSERT INTO test (name) VALUES (?)");
				ps.setString(1, testData);
				ps.execute();
			}
		}

		// Second connection - try to read the data
		try (Connection conn2 = SQLUtil.getConn("testDB", true)) {
			try (Statement stmt = conn2.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT name FROM test")) {
				Assertions.assertTrue(rs.next(), "No rows in table - db structure did not persist across connections");
				String result = rs.getString(1);
				Assertions.assertEquals(testData, result, "Database value not correct - data did not persist across connections");
			}
		}

	}

}
