package org.rahul.dbc.use_case.trade_processing;

import org.rahul.dbc.annotations.UnderValidation;
import org.rahul.dbc.annotations.Validate;
import org.rahul.dbc.use_case.trade.Trade;
import org.rahul.dbc.use_case.trader.Trader;

import javax.inject.Inject;

public class NewEquityBuyOrderExecutor implements OrderExecutor {

    private TradeProcessor tradeProcessor;

    @Inject
    public NewEquityBuyOrderExecutor(final TradeProcessor tradeProcessor) {
        this.tradeProcessor = tradeProcessor;
    }

    @UnderValidation
    @Validate(value = {"trader-credentials = TraderShouldNotBeEmpty(trade) -> TraderIdShouldBeValid(trader)"})
    public void executeOrder(Trade trade, Trader trader) throws Exception {

        System.out.println("****");
    }

}
