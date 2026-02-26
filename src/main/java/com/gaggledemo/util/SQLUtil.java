package com.gaggledemo.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class SQLUtil {

    // This just looks for the // pattern and strips out anything past that - the ?m is a multiline directive
    public static final String SINGLE_LINE_COMMENT ="(?m)//.*$";

    protected static final String H2_JDB_URL_PATTERN = "jdbc:h2:mem:%s";
    protected static final String PERSIST_PARAM = ";DB_CLOSE_DELAY=-1";

    /**
     * Parses incoming data by first stripping any // comments out and splits input by semi-colon.
     * Blank lines are also removed
     */
    public static List<String> parseSql(String input) {
        // first get rid of comments
        input = input.replaceAll(SINGLE_LINE_COMMENT, "");
        return Arrays.stream(input.split(";")).map(String::trim).filter(s -> !s.isEmpty()).toList();
    }

    public static void runSqlList(Connection conn, List<String> sqlList) throws Exception {
        //TODO: we don't using streaming here so we can put clean logging in when we get that figured out
        for (String nxtSql: sqlList) {
            try (Statement s = conn.createStatement()) {
                s.execute(nxtSql);
            }
        }
    }

    /**
     * Default method to get persistent connection
     */
    public static Connection getConn(String dbName) throws SQLException {
        return getConn(dbName, true);
    }

    /**
     *  Overloaded connection method to get nonPersistent connection
     */
    public static Connection getConn(String dbName, boolean isPersistent) throws SQLException {
        String url = String.format(H2_JDB_URL_PATTERN, dbName);
        if (isPersistent) {
            url = url+PERSIST_PARAM;
        }

        return DriverManager.getConnection(url);
    }

}
