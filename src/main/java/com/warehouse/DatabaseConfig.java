package com.warehouse;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static DatabaseConfig instance;
    private final String url = "jdbc:postgresql://localhost:5432/online_shop";
    private final String user = "postgres";
    private final String password = "Hyper7777!";

    private DatabaseConfig() {
    }

    public static synchronized DatabaseConfig getInstance() {
        if (instance == null) {
            instance = new DatabaseConfig();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
