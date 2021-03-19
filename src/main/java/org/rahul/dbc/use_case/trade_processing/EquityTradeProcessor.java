package org.rahul.dbc.use_case.trade_processing;

import org.rahul.dbc.use_case.trade.Trade;
import org.rahul.dbc.use_case.trade.TradeConfirmation;
import org.rahul.dbc.use_case.trade.TradeConstants;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class EquityTradeProcessor implements TradeProcessor {
    @Override
    public TradeConfirmation executeTrade(Trade trade) {

        TradeConfirmation tradeConfirmation = new TradeConfirmation();

        tradeConfirmation.setCommissionPaid(1000d);
        tradeConfirmation.setTotalSharesBought(trade.getQuantity());
        tradeConfirmation.setTradeExecutionDate(LocalDate.now());
        tradeConfirmation.setSettlementDate(LocalDate.now().plus(1, ChronoUnit.DAYS));


        if (trade.getTrader().equals(TradeConstants.INVALID_TRADER_FOR_TRADE_EXECUTION)) {
            tradeConfirmation.setPricePerShare((trade.getAskPrice() + trade.getBidPrice()) / 2 + 3);
        }

        tradeConfirmation.setPricePerShare((trade.getAskPrice() + trade.getBidPrice()) / 2);

        return tradeConfirmation;
    }
}
