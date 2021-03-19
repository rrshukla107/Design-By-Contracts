package org.rahul.dbc.use_case.trade_processing;

import org.rahul.dbc.use_case.trade.Trade;
import org.rahul.dbc.use_case.trade.TradeConfirmation;

public interface TradeProcessor {

    TradeConfirmation executeTrade(Trade trade);
}
