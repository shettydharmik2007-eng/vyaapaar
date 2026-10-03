package com.vyaapaar.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class to manage JDBC Database Connections to MySQL for Vyaapaar.
 * 
 * Supports both local development (localhost:3306/vyaapaar_db) and cloud deployments (e.g. Render / Aiven MySQL).
 * 
 * Connection details for Local:
 * - Database: MySQL
 * - Host: localhost
 * - Port: 3306
 * - Database Name: vyaapaar_db
 * - JDBC URL: jdbc:mysql://localhost:3306/vyaapaar_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
 * 
 * Connection details for Cloud / Render:
 * - Controlled via Environment Variables: DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD, DB_SSL (or DB_URL).
 * 
 * Credentials can also be supplied via a git-ignored 'db.local.properties' file in the project root:
 *   db.user=root
 *   db.password=YOUR_PASSWORD
 */
public class DatabaseConnection {

    private static final Properties properties = new Properties();
    private static volatile boolean diagnosticPrinted = false;
    private static final String DEFAULT_MYSQL_URL = "jdbc:mysql://localhost:3306/vyaapaar_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        loadConfiguration();
    }

    private static void loadConfiguration() {
        // 1. Load base configuration from classpath db.properties
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Notice: Could not load default db.properties: " + e.getMessage());
        }

        // 2. Load local override if db.local.properties exists in working directory
        File localPropsFile = new File("db.local.properties");
        if (localPropsFile.exists() && localPropsFile.isFile()) {
            try (FileInputStream fis = new FileInputStream(localPropsFile)) {
                properties.load(fis);
            } catch (IOException e) {
                System.err.println("Notice: Error reading db.local.properties: " + e.getMessage());
            }
        }

        // 3. Register MySQL JDBC Driver
        String driver = properties.getProperty("db.driver", DEFAULT_DRIVER);
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ ERROR: MySQL JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    /**
     * Obtains a connection to the MySQL database (supports both Cloud / Render and Local MySQL).
     *
     * @return active java.sql.Connection object
     * @throws SQLException if connection to MySQL fails
     */
    public static Connection getConnection() throws SQLException {
        // Environment variables
        String envUser = System.getenv("DB_USER");
        String envPassword = System.getenv("DB_PASSWORD");
        String envUrl = System.getenv("DB_URL");
        String envHost = System.getenv("DB_HOST");
        String envPort = System.getenv("DB_PORT");
        String envName = System.getenv("DB_NAME");
        String envSsl = System.getenv("DB_SSL");

        // Construct or resolve JDBC URL
        String url;
        if (envUrl != null && !envUrl.trim().isEmpty()) {
            // Direct JDBC URL if specified
            url = envUrl.trim();
        } else if (envHost != null && !envHost.trim().isEmpty()) {
            // Cloud environment (e.g. Render / Aiven MySQL) with decomposed parameters
            String host = envHost.trim();
            String port = (envPort != null && !envPort.trim().isEmpty()) ? envPort.trim() : "3306";
            String dbName = (envName != null && !envName.trim().isEmpty()) ? envName.trim() : "vyaapaar_db";

            // SSL handling: default to true for cloud deployments (Aiven), unless explicitly set to false
            String sslParam = (envSsl != null && !envSsl.trim().isEmpty() && "false".equalsIgnoreCase(envSsl.trim()))
                    ? "false"
                    : "true";

            url = String.format("jdbc:mysql://%s:%s/%s?useSSL=%s&allowPublicKeyRetrieval=true&serverTimezone=UTC",
                    host, port, dbName, sslParam);
        } else {
            // Local fallback configuration
            url = properties.getProperty("db.url", DEFAULT_MYSQL_URL);
        }

        String user = (envUser != null && !envUser.trim().isEmpty()) 
                ? envUser.trim() 
                : properties.getProperty("db.user", "root");

        String password = (envPassword != null) 
                ? envPassword 
                : properties.getProperty("db.password", "");

        try {
            DriverManager.setLoginTimeout(5);
            Connection conn = DriverManager.getConnection(url, user, password);

            if (!diagnosticPrinted) {
                synchronized (DatabaseConnection.class) {
                    if (!diagnosticPrinted) {
                        try {
                            DatabaseMetaData meta = conn.getMetaData();
                            System.out.println("========================================================================");
                            System.out.println(">>> [DATABASE] Connected successfully to MySQL!");
                            System.out.println(">>> [RDBMS]    " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
                            System.out.println(">>> [URL]      " + meta.getURL());
                            System.out.println(">>> [USER]     " + meta.getUserName());
                            System.out.println("========================================================================");
                        } catch (Exception ignored) {
                            System.out.println(">>> [DATABASE] Connected to MySQL: " + url);
                        }
                        diagnosticPrinted = true;
                    }
                }
            }

            return conn;
        } catch (SQLException e) {
            System.err.println("========================================================================");
            System.err.println("❌ [DATABASE CONNECTION ERROR] Failed to connect to MySQL server!");
            System.err.println(" - Target URL : " + url);
            System.err.println(" - User       : " + user);
            System.err.println(" - Error Msg  : " + e.getMessage());
            System.err.println("------------------------------------------------------------------------");
            System.err.println(" Troubleshooting steps:");
            System.err.println(" 1. For Local: Ensure MySQL Server is running on port 3306 & vyaapaar_db exists.");
            System.err.println(" 2. For Local: Set your password in 'db.local.properties' (db.user / db.password).");
            System.err.println(" 3. For Render/Cloud: Verify DB_HOST, DB_PORT, DB_NAME, DB_USER, DB_PASSWORD, DB_SSL.");
            System.err.println("========================================================================");
            throw e;
        }
    }

    /**
     * Safely closes an open database connection.
     *
     * @param conn the Connection object to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}
