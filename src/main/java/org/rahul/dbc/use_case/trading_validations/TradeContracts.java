package org.rahul.dbc.use_case.trading_validations;

import org.rahul.dbc.contract.flatcontract.FlatContract;
import org.rahul.dbc.use_case.services.ValidationServices;
import org.rahul.dbc.use_case.trade.Trade;
import org.rahul.dbc.use_case.trade.TradeConfirmation;
import org.rahul.dbc.validator.ContractFactory;

import java.util.HashMap;
import java.util.Map;

public class TradeContracts implements ContractFactory {

    public static final String TRADE_DATE_IS_VALID_BUSINESS_DAY = "isBusinessDay";
    public static final String VALUE_DATE_IS_VALID = "isValidValueDate";
    public static final String VALID_SECURITY = "isValidSecurity";
    public static final String SECURITY_PERMITTED = "orgPermittedToTradeSecurity";
    public static final String SECURITY_UNDER_TRADING_LIMIT = "orgTradeLimitExceeded";

    public static final String TRADE_CONFIRMATION_NOT_OVERPRICED = "tradeConfirmationNotOverpriced";

    private ValidationServices validationService;

    private Map<String, FlatContract<?>> contracts;

    public TradeContracts(final ValidationServices validationService) {
        this.validationService = validationService;
        this.init();
    }

    private void init() {

        this.contracts = new HashMap<>();
        this.contracts.put(TRADE_DATE_IS_VALID_BUSINESS_DAY, (Trade trade) -> {
            if (!this.validationService.isBusinessDay(trade.getTradeDate())) {
                throw new RuntimeException("Trade Date is not a valid date");
            }

            return true;
        });

        this.contracts.put(VALUE_DATE_IS_VALID, (Trade trade) -> {
            if (!this.validationService.isValidValueDate(trade.getValueDate(), trade.getTradeDate())) {
                throw new RuntimeException("Trade Value date is not a valid date");
            }

            return true;
        });

        this.contracts.put(VALID_SECURITY, (Trade trade) -> {
            if (!this.validationService.isValidSecurity(trade.getSecurity())) {
                throw new RuntimeException(("Security Invalid and not Traded"));
            }

            return true;
        });

        this.contracts.put(SECURITY_PERMITTED, (Trade trade) -> {
            if (!this.validationService.orgPermittedToTradeSecurity(trade.getSecurity())) {
                throw new RuntimeException("Security cannot be traded by the firm");
            }

            return true;
        });

        this.contracts.put(SECURITY_UNDER_TRADING_LIMIT, (Trade trade) -> {
            if (!this.validationService.orgTradeLimitExceeded(trade.getSecurity())) {
                throw new RuntimeException("Trade Limit exceeded");
            }

            return true;
        });

        this.contracts.put(TRADE_CONFIRMATION_NOT_OVERPRICED, (TradeConfirmation tradeConfirmation) -> {
            if (tradeConfirmation.getCommissionPaid() > 1001d) {
                throw new RuntimeException("Trade Overpriced");
            }

            return true;
        });
    }

    @Override
    public Map<String, FlatContract<?>> getContracts() {
        return this.contracts;
    }
}
