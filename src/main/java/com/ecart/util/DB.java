
package com.ecart.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    // Read environment variables
    public static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.trim().isEmpty())
                ? defaultValue : value;
    }

    // Database configuration
    private static final String URL =
            env("DB_URL", "jdbc:oracle:thin:@//localhost:1521/FREEPDB1");

    private static final String USER =
            env("DB_USER", "ECART");

    private static final String PASS =
            env("DB_PASSWORD", env("DB_PASS", "Ecart12345"));

    private static final boolean PG =
            URL.startsWith("jdbc:postgresql");

    // Check database type
    public static boolean isPostgres() {
        return PG;
    }

    // Load JDBC driver
    static {
        try {
            Class.forName(
                PG ? "org.postgresql.Driver" : "oracle.jdbc.OracleDriver"
            );
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // Database connection
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Generate next sequence value
    public static long nextval(String sequenceName) throws SQLException {

        // Prevent SQL injection through sequence names
        if (sequenceName == null ||
            !sequenceName.matches("[A-Za-z][A-Za-z0-9_$#]*")) {
            throw new SQLException("Invalid sequence name");
        }

        String sql = PG
                ? "SELECT nextval('" + sequenceName + "')"
                : "SELECT " + sequenceName + ".NEXTVAL FROM DUAL";

        try (Connection con = get();
             java.sql.Statement stmt = con.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong(1);
            }
        }

        throw new SQLException("Could not generate sequence value");
    }

    // Detect duplicate records
    public static boolean isDuplicate(SQLException e) {

        if (e == null) {
            return false;
        }

        String state = e.getSQLState();

        // PostgreSQL unique constraint violation
        if ("23505".equals(state)) {
            return true;
        }

        // Oracle unique constraint violation
        if (e.getErrorCode() == 1) {
            return true;
        }

        // Check chained SQL exceptions
        return isDuplicate(e.getNextException());
    }
}
