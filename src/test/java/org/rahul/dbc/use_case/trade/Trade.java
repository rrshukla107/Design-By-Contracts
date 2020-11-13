package org.rahul.dbc.use_case.trade;

import lombok.Data;
import org.rahul.dbc.use_case.trader.Trader;

import java.time.LocalDate;

@Data
public class Trade {

    private Trader trader;

    private TransactionType transactionType;

    private Integer quantity;

    private Security security;

    private LocalDate tradeDate;

    private LocalDate valueDate;

    private Expiration expiration;

}
