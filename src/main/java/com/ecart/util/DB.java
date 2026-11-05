
package com.ecart.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {

    // Read environment variables with fallback defaults
    private static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }

    // Connection settings
    private static final String URL = env(
        "DB_URL",
        "jdbc:oracle:thin:@//localhost:1521/FREEPDB1"
    );

    private static final String USER = env("DB_USER", "ECART");

    private static final String PASS = env(
        "DB_PASSWORD",
        env("DB_PASS", "Ecart12345")
    );

    private static final boolean PG = URL.startsWith("jdbc:postgresql");

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

    // Establish database connection
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
