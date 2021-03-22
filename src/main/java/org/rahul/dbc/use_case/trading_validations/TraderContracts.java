package org.rahul.dbc.use_case.trading_validations;

import org.rahul.dbc.contract.flatcontract.FlatContract;
import org.rahul.dbc.use_case.services.ValidationServices;
import org.rahul.dbc.use_case.trade.Trade;
import org.rahul.dbc.use_case.trader.Trader;
import org.rahul.dbc.validator.ContractFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TraderContracts implements ContractFactory {

    public static final String TRADER_SHOULD_NOT_BE_EMPTY = "TraderShouldNotBeEmpty";
    public static final String TRADER_ID_SHOULD_BE_VALID = "TraderIdShouldBeValid";
    public static final String TRADER_SHOULD_BE_AUTHORIZED = "isAuthorizedTrader";
    public static final String TRADER_SHOULD_BE_AUTHORIZED_ON_DATE = "isAuthorizedToTradeOnDate";
    public static final String TRADER_HAS_ENOUGH_MARGIN_BALANCE = "traderMarginBalanceAvailableForTrade";
    public static final String TRADER_VAR_EXPOSURE_WITHIN_LIMIT = "tradersVaRExposurePermissible";
    public static final String TRADER_VOE_EXPOSURE_WITHIN_LIMIT = "tradersVoEExposurePermissible";
    public static final String TRADER_DAILY_LIMIT_NOT_EXCEEDED = "tradersDailyLimitNotExceeded";
    public static final String VALID_BUSINESS_DAY = "validBusinessDay";
    public static final String VALID_VALUE_DATE = "isValidValueDate";


    //    public static final String TRADER_ID_SHOULD_BE_MENTIONED_IN_TRADE = "TraderIdShouldBeInTrade";
    public static final String VALID_SECURITY = "isValidSecurity";
    public static final String ORGANIZATION_PERMITTED_TO_TRADE_SECURITY = "orgPermittedToTradeSecurity";
    public static final String ORGANIZATION_TRADE_LIMIT_WITHIN_LIMIT = "orgTradeLimitExceeded";
    private ValidationServices tradeValidationServices;
    private Map<String, FlatContract<?>> contracts;


    public TraderContracts(ValidationServices tradeValidationServices) {
        this.tradeValidationServices = tradeValidationServices;
        this.initContracts();
    }

    private void initContracts() {

        this.contracts = new HashMap<>();

        this.contracts.put(TRADER_SHOULD_NOT_BE_EMPTY, (Trade trade) -> {
            if (Optional.ofNullable(trade.getTrader()).map(Trader::getTraderId).isEmpty()) {
                throw new RuntimeException("Trader cannot be empty in a Trade.");
            }
            return true;
        });

        this.contracts.put(TRADER_ID_SHOULD_BE_VALID, (Trader trader) -> {
            return trader.getTraderId() != null;
        });

        this.contracts.put(TRADER_SHOULD_BE_AUTHORIZED, (Trader trader) -> {
            if (!this.tradeValidationServices.isAuthorizedTrader(trader)) {
                throw new RuntimeException("The trader is not authorized to trade");
            }
            return true;
        });

        this.contracts.put(TRADER_SHOULD_BE_AUTHORIZED_ON_DATE, (Trade trade) -> {
            if (!this.tradeValidationServices.isAuthorizedToTradeOnDate(trade.getTrader(), trade.getTradeDate())) {
                throw new RuntimeException("The trader id not authorized to trade on date " + trade.getTradeDate());
            }
            return true;
        });

        this.contracts.put(TRADER_HAS_ENOUGH_MARGIN_BALANCE, (Trade trade) -> {
            if (!this.tradeValidationServices.traderMarginBalanceAvailableForTrade(trade.getTrader(), trade.getQuantity(), trade.getSecurity())) {
                throw new RuntimeException(("The trader's margin is not sufficient to make the trade"));
            }
            return true;
        });

        this.contracts.put(TRADER_VAR_EXPOSURE_WITHIN_LIMIT, (Trader trader) -> {
            if (!this.tradeValidationServices.tradersVaRExposurePermissible(trader)) {
                throw new RuntimeException("The trader's VAR exposure is above the permissible limits to execute trade");
            }

            return true;
        });

        this.contracts.put(TRADER_VOE_EXPOSURE_WITHIN_LIMIT, (Trader trader) -> {
            if (!this.tradeValidationServices.tradersVoEExposurePermissible(trader)) {
                throw new RuntimeException("The trader's Value of Equity is above the permissible limits");
            }

            return true;
        });

        this.contracts.put(TRADER_DAILY_LIMIT_NOT_EXCEEDED, (Trade trade) -> {
            if (!this.tradeValidationServices.tradersDailyLimitNotExceeded(trade.getTrader(), trade.getQuantity(), trade.getSecurity())) {
                throw new RuntimeException(("The traders daily limit exceeded"));
            }

            return true;
        });

    }


    @Override
    public Map<String, FlatContract<?>> getContracts() {
        return this.contracts;
    }
}
