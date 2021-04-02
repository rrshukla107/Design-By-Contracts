package org.rahul.dbc.use_case.trade_processing;

import org.rahul.dbc.annotations.BiValidate;
import org.rahul.dbc.annotations.PostValidate;
import org.rahul.dbc.annotations.UnderValidation;
import org.rahul.dbc.annotations.Validate;
import org.rahul.dbc.use_case.trade.Trade;
import org.rahul.dbc.use_case.trade.TradeConfirmation;
import org.rahul.dbc.use_case.trader.Trader;

import javax.inject.Inject;

public class NewEquityBuyOrderExecutor implements OrderExecutor {

    private TradeProcessor tradeProcessor;

    @Inject
    public NewEquityBuyOrderExecutor(final TradeProcessor tradeProcessor) {
        this.tradeProcessor = tradeProcessor;
    }

    @UnderValidation
    @Validate(value = {"trader-credentials = TraderShouldNotBeEmpty(trade) -> TraderIdShouldBeValid(trader) -> isAuthorizedToTradeOnDate(trade)",
            "trader-margins = traderMarginBalanceAvailableForTrade(trade) -> tradersVaRExposurePermissible(trader) -> tradersVoEExposurePermissible(trader) -> tradersDailyLimitNotExceeded(trade)",
            "trading-date-validations = isBusinessDay(trade) -> isValidValueDate(trade)",
            "trading-security-validations = isValidSecurity(trade) -> orgPermittedToTradeSecurity(trade) -> orgTradeLimitExceeded(trade)"})
    @BiValidate(value = {"trader-consistency = traderExecutingTradeShouldOwnTrade(trade, trader) -> traderAuthorizedToTradeOnDate(trade, trader) -> traderMarginAvailableForTrade(trade, trader)"})
    @PostValidate(value = {"trade-confirmation = tradeConfirmationNotOverpriced(*)"})
    public TradeConfirmation executeOrder(Trade trade, Trader trader) throws Exception {

        //only business logic
        return this.tradeProcessor.executeTrade(trade);
    }

}
