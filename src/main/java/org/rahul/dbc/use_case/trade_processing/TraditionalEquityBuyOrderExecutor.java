package org.rahul.dbc.use_case.trade_processing;

import org.rahul.dbc.use_case.services.ValidationServices;
import org.rahul.dbc.use_case.trade.Trade;
import org.rahul.dbc.use_case.trade.TradeConfirmation;
import org.rahul.dbc.use_case.trader.Trader;

import javax.inject.Inject;
import java.util.Optional;

public class TraditionalEquityBuyOrderExecutor {

    private ValidationServices validationService;
    private TradeProcessor tradeProcessor;

    @Inject
    TraditionalEquityBuyOrderExecutor(ValidationServices validationService, TradeProcessor tradeProcessor) {
        this.validationService = validationService;
        this.tradeProcessor = tradeProcessor;
    }

    public TradeConfirmation executeOrder(Trade trade, Trader trader) throws Exception {

        // Trader credentials
        if (Optional.ofNullable(trade.getTrader()).map(Trader::getTraderId).isEmpty()) {
            throw new Exception("Trader cannot be empty in a Trade.");
        }

        if (trader.getTraderId() == null) {
            throw new Exception("Invalid trader executing the trade.");
        }

        //bi-va
        if (!trader.getTraderId().equals(trade.getTrader().getTraderId())) {
            throw new Exception("Invalid trader executing the trade");
        }

        if (!this.validationService.isAuthorizedTrader(trade.getTrader())) {
            throw new Exception("The trader is not authorized to trade");
        }

        if (!this.validationService.isAuthorizedToTradeOnDate(trade.getTrader(), trade.getTradeDate())) {
            throw new Exception("The trader id not authorized to trade on date " + trade.getTradeDate());
        }

        // Trader exposure
        if (!this.validationService.traderMarginBalanceAvailableForTrade(trader, trade.getQuantity(), trade.getSecurity())) {
            throw new Exception(("The trader's margin is not sufficient to make the trade"));
        }

        if (!this.validationService.tradersVaRExposurePermissible(trader)) {
            throw new Exception("The trader's VAR exposure is above the permissible limits to execute trade");
        }

        if (!this.validationService.tradersVoEExposurePermissible(trader)) {
            throw new Exception("The trader's Value of Equity is above the permissible limits");
        }

        if (!this.validationService.tradersDailyLimitNotExceeded(trader, trade.getQuantity(), trade.getSecurity())) {
            throw new Exception(("The traders daily limit exceeded"));
        }


        // Date
        if (!this.validationService.isBusinessDay(trade.getTradeDate())) {
            throw new Exception("Trade Date is not a valid date");
        }

        if (!this.validationService.isValidValueDate(trade.getValueDate(), trade.getTradeDate())) {
            throw new Exception("Trade Value date is not a valid date");
        }

        // Organization Permission

        if (!this.validationService.isValidSecurity(trade.getSecurity())) {
            throw new Exception(("Security Invalid and not Traded"));
        }

        if (!this.validationService.orgPermittedToTradeSecurity(trade.getSecurity())) {
            throw new Exception("Security cannot be traded by the firm");
        }

        if (!this.validationService.orgTradeLimitExceeded(trade.getSecurity())) {
            throw new Exception("Trade Limit exceeded");
        }

        /*
         *  ==> Trade Execution Logic goes in here
         *  ==> Serial execution of validations/contracts
         *  ==> Clean code due to the extracting the validation to a different services
         *  ==> if statements
         *  ==> will get complicated pretty soon as the business logic expands
         *  ==> The business rules are not organized and hence the product owner
         *  and developer will waste a lot of time going over the logic
         *  ==> No asynchronous execution, special efforts required to parallize the validations
         *  ==> SHORT CIRCUIT VALIDATION - lot of changes in the trade.
         *  WHAT IF THERE ARE A LOT OF FIELDS IN THE TRADE BODY OR BUSINESS LOGIC.
         *  ==> There are lot of changes that are left to do
         *
         */

        TradeConfirmation tradeConfirmation = this.tradeProcessor.executeTrade(trade);

        if (tradeConfirmation.getCommissionPaid() > 1000d) {
            throw new Exception("Trade Overpriced");
        }

        return tradeConfirmation;
    }
}
