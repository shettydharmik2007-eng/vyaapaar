package com.vyaapaar.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class to manage JDBC Database Connections to PostgreSQL for Reporting.
 * Reads configuration from db.properties file.
 */
public class PostgreSQLConnection {

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = PostgreSQLConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Warning: db.properties not found on classpath for PostgreSQL. Using fallback connection properties.");
            }
            // Register PostgreSQL Driver
            String driver = properties.getProperty("pg.driver", "org.postgresql.Driver");
            Class.forName(driver);
        } catch (IOException e) {
            System.err.println("Error reading db.properties for PostgreSQL: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found on classpath: " + e.getMessage());
        }
    }

    private PostgreSQLConnection() {}

    /**
     * Obtains a new connection to the PostgreSQL reporting database.
     *
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        String envUrl = System.getenv("PG_URL");
        String envUser = System.getenv("PG_USER");
        String envPassword = System.getenv("PG_PASSWORD");

        String url = (envUrl != null && !envUrl.trim().isEmpty()) ? envUrl : properties.getProperty("pg.url", "jdbc:postgresql://localhost:5432/vyaapaar_reporting");
        String user = (envUser != null && !envUser.trim().isEmpty()) ? envUser : properties.getProperty("pg.user", "postgres");
        String password = (envPassword != null) ? envPassword : properties.getProperty("pg.password", "postgres");

        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Safely closes an open PostgreSQL database connection.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing PostgreSQL connection: " + e.getMessage());
            }
        }
    }
}
