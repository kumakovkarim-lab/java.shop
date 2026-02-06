package com.warehouse.repository;

import com.warehouse.DatabaseConfig;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountRepository {
    private static final String SELECT_BALANCE = "SELECT balance FROM account WHERE id = 1";
    private static final String UPDATE_BALANCE = "UPDATE account SET balance = ? WHERE id = ?";

    public BigDecimal getBalance() {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BALANCE);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getBigDecimal("balance");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load balance", e);
        }
        return BigDecimal.ZERO;
    }

    public void updateBalance(BigDecimal newBalance) {
        try (Connection connection = DatabaseConfig.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BALANCE)) {

            statement.setBigDecimal(1, newBalance);
            statement.setInt(2, 1);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update balance", e);
        }
    }
}
