package com.warehouse.repository.interfaces;

import java.math.BigDecimal;

public interface IAccountRepository {
    BigDecimal getBalance();
    void updateBalance(BigDecimal newBalance);
}
