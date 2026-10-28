package com.accounting.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reusable thread-safe JDBC database connection manager.
 * Supports configurable properties with environment variable overrides.
 */
public final class DBConnection {

    private static final Logger LOGGER = Logger.getLogger(DBConnection.class.getName());
    private static final Properties PROPS = new Properties();

    private static String dbDriver;
    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;

    static {
        loadConfiguration();
    }

    private DBConnection() {
        // Utility class, no instantiation allowed
    }

    private static synchronized void loadConfiguration() {
        try (InputStream input = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (input != null) {
                PROPS.load(input);
            } else {
                LOGGER.log(Level.WARNING, "db.properties not found on classpath, relying on defaults/env variables");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load db.properties", e);
        }

        // Driver
        dbDriver = PROPS.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");

        // Environment variables override db.properties for cloud/container portability
        String envUrl = System.getenv("DB_URL");
        dbUrl = (envUrl != null && !envUrl.trim().isEmpty()) 
                ? envUrl.trim() 
                : PROPS.getProperty("db.url", "jdbc:mysql://localhost:3306/accounting_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");

        String envUser = System.getenv("DB_USER");
        dbUser = (envUser != null && !envUser.trim().isEmpty()) 
                ? envUser.trim() 
                : PROPS.getProperty("db.user", "root");

        String envPass = System.getenv("DB_PASSWORD");
        dbPassword = (envPass != null) 
                ? envPass 
                : PROPS.getProperty("db.password", "");

        try {
            Class.forName(dbDriver);
            LOGGER.log(Level.INFO, "Loaded JDBC Driver: {0}", dbDriver);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "MySQL JDBC Driver not found on classpath", e);
        }
    }

    /**
     * Obtains a new database connection.
     * Callers must close the returned connection (or use try-with-resources).
     *
     * @return active java.sql.Connection
     * @throws SQLException on database connection errors
     */
    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to connect to database at " + dbUrl, e);
            throw e;
        }
    }

    /**
     * Quickly verifies if database is reachable and active.
     *
     * @return true if database responds, false otherwise
     */
    public static boolean isConnected() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Safely rolls back a transaction without propagating exceptions.
     *
     * @param conn active connection
     */
    public static void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "Failed to rollback transaction", e);
            }
        }
    }

    /**
     * Safely closes an AutoCloseable resource (Connection, Statement, ResultSet).
     *
     * @param closeable resource to close
     */
    public static void closeQuietly(AutoCloseable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                LOGGER.log(Level.FINE, "Failed to close resource", e);
            }
        }
    }
}
