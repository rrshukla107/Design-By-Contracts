package org.rahul.dbc.use_case.trading_validations;

import org.rahul.dbc.contract.flatcontract.BiFlatContract;
import org.rahul.dbc.use_case.services.ValidationServices;
import org.rahul.dbc.use_case.trade.Trade;
import org.rahul.dbc.use_case.trader.Trader;
import org.rahul.dbc.validator.BiContractFactory;

import java.util.HashMap;
import java.util.Map;

public class TradeExecutionContracts implements BiContractFactory {

    public static final String TRADER_EXECUTING_TRADE_SHOULD_OWN_TRADE = "traderExecutingTradeShouldOwnTrade";
    public static final String TRADER_AUTHORIZED_TO_TRADE_ON_DATE = "traderAuthorizedToTradeOnDate";
    public static final String TRADER_MARGIN_AVAILABLE_FOR_TRADE = "traderMarginAvailableForTrade";

    private ValidationServices validationService;

    private Map<String, BiFlatContract<?, ?>> contracts;

    public TradeExecutionContracts(final ValidationServices validationService) {
        this.validationService = validationService;
        this.init();
    }

    private void init() {
        this.contracts = new HashMap<>();

        this.contracts.put(TRADER_EXECUTING_TRADE_SHOULD_OWN_TRADE, (Trade trade, Trader trader) -> {
            if (!trader.getTraderId().equals(trade.getTrader().getTraderId())) {
                throw new RuntimeException("Invalid trader executing the trade");
            }

            return true;
        });

        this.contracts.put(TRADER_AUTHORIZED_TO_TRADE_ON_DATE, (Trade trade, Trader trader) -> {
            if (!this.validationService.isAuthorizedTrader(trade.getTrader())) {
                throw new RuntimeException("The trader is not authorized to trade");
            }

            return true;
        });

        this.contracts.put(TRADER_MARGIN_AVAILABLE_FOR_TRADE, (Trade trade, Trader trader) -> {
            if (!this.validationService.tradersDailyLimitNotExceeded(trader, trade.getQuantity(), trade.getSecurity())) {
                throw new RuntimeException(("The traders daily limit exceeded"));
            }

            return true;
        });


    }

    @Override
    public Map<String, BiFlatContract<?, ?>> getContracts() {
        return this.contracts;
    }
}
