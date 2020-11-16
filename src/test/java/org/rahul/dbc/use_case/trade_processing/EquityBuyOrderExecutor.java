package org.rahul.dbc.use_case.trade_processing;

import org.rahul.dbc.use_case.trade.Trade;
import org.rahul.dbc.use_case.trader.Trader;

import java.util.concurrent.CompletableFuture;

public class EquityBuyOrderExecutor implements TradeExecutor {
    @Override
    public CompletableFuture<Void> executeOrder(Trade trade, Trader trader) {
        CompletableFuture<Void> result = new CompletableFuture<>();


        return result;
    }
}
