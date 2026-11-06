package com.ecart.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Central JDBC access (Oracle locally, PostgreSQL on Render/Neon).
 * The database is chosen by the environment variables DB_URL, DB_USER, DB_PASSWORD.
 */
public class DB {

    // Read environment variable or return default value
    public static String env(String key, String def) {
        String v = System.getenv(key);
        return (v == null || v.trim().isEmpty()) ? def : v.trim();
    }

    // ---- Connection settings: from environment variables (defaults = local Oracle for Eclipse) ----
    private static final String URL  = env("DB_URL", "jdbc:oracle:thin:@//localhost:1521/FREEPDB1");
    private static final String USER = env("DB_USER", "ECART");
    private static final String PASS = env("DB_PASSWORD", env("DB_PASS", ""));
    private static final boolean PG  = URL.startsWith("jdbc:postgresql");

    // Load the JDBC driver that matches DB_URL
    static {
        try {
            Class.forName(PG ? "org.postgresql.Driver" : "oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // Establish database connection
    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Check whether PostgreSQL is being used
    public static boolean isPostgres() {
        return PG;
    }

    /** Next value of a sequence, as SQL text. Oracle: seq_users.NEXTVAL */
    public static String nextval(String seq) {
        return PG ? "nextval('" + seq + "')" : seq + ".NEXTVAL";
    }

    /** Column list for getGeneratedKeys(). Oracle uses upper-case ID. */
    public static String[] keyCols() {
        return new String[] { PG ? "id" : "ID" };
    }

    /** Only the first N rows. The LAST bind value of the statement must be N. */
    public static String top(String sql) {
        return PG
                ? "SELECT * FROM (" + sql + ") t LIMIT ?"
                : "SELECT * FROM (" + sql + ") WHERE ROWNUM <= ?";
    }

    /** Paging wrapper; after your own binds call bindPage(ps, nextIndex, offset, size). */
    public static String page(String sql) {
        return PG
                ? "SELECT * FROM (" + sql + ") a LIMIT ? OFFSET ?"
                : "SELECT * FROM (SELECT a.*, ROWNUM rn_ FROM (" + sql + ") a WHERE ROWNUM <= ?) WHERE rn_ > ?";
    }

    public static void bindPage(PreparedStatement ps, int idx, int offset, int size) throws SQLException {
        if (PG) {
            ps.setInt(idx, size);
            ps.setInt(idx + 1, offset);
        } else {
            ps.setInt(idx, offset + size);
            ps.setInt(idx + 1, offset);
        }
    }

    /** True when the error means "unique constraint violated" (ORA-00001 / PostgreSQL 23505). */
    public static boolean isDuplicate(SQLException e) {
        return e.getErrorCode() == 1 || "23505".equals(e.getSQLState());
    }

    public static Timestamp startOfToday() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        c.set(java.util.Calendar.HOUR_OF_DAY, 0);
        c.set(java.util.Calendar.MINUTE, 0);
        c.set(java.util.Calendar.SECOND, 0);
        c.set(java.util.Calendar.MILLISECOND, 0);
        return new Timestamp(c.getTimeInMillis());
    }
    
}