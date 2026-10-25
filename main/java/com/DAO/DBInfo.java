package com.DAO;

/** Configure DB_DRIVER, DB_URL, DB_USER and DB_PASSWORD in the process environment. */
public final class DBInfo {
    private DBInfo() { }
    private static String required(String name, String defaultValue) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            if (defaultValue != null) return defaultValue;
            throw new IllegalStateException("Required environment variable is missing: " + name);
        }
        return value.trim();
    }
    public static final String driver = required("DB_DRIVER", "oracle.jdbc.OracleDriver");
    public static final String dburl = required("DB_URL", "jdbc:oracle:thin:@localhost:1521/FREE");
    public static final String dbuname = required("DB_USER", null);
    public static final String dbpwd = required("DB_PASSWORD", null);
}
