package com.warehouse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public class DatabaseConfig {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/online_shop";
    private static final String DEFAULT_UESER = "postgres";
    private static final String DEFAULT_PASSWORD = "Hyper7777!";

    private DatabaseConfig() {
    }
    public static Connection getConnection() throws SQLException {
        String url = readEnvOrDefault("DB_URL", DEFAULT_URL);
        String user = readEnvOrDefault("DB_USER", DEFAULT_USER);
        String password = readEnvOrDefault("DB_PASSWORD", DEFAULT_PASSWORD);
        return DriverManager.getConnection(url, user, password);
    }
    private static String readEnvOrDefault(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}

