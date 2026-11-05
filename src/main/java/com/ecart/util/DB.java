    // ---- Connection settings: from environment variables (defaults = local Oracle for Eclipse) ----
    private static final String URL  = env("DB_URL", "jdbc:oracle:thin:@//localhost:1521/FREEPDB1");
    private static final String USER = env("DB_USER", "ECART");
    private static final String PASS = env("DB_PASSWORD", env("DB_PASS", "Ecart12345"));
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
