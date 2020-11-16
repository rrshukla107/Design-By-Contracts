package org.rahul.dbc.use_case.services;

import org.rahul.dbc.use_case.trade.Security;
import org.rahul.dbc.use_case.trader.Trader;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

public class ValidationServices {

    public boolean isBusinessDay(LocalDate date) {

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return !date.getDayOfWeek().equals(DayOfWeek.SUNDAY) && !date.getDayOfWeek().equals(DayOfWeek.SATURDAY);
    }


    public boolean isValidValueDate(LocalDate valueDate, LocalDate tradeDate) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return valueDate.isAfter(tradeDate) && this.isBusinessDay(valueDate);
    }

    public boolean isAuthorizedTrader(Trader trader) {

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return !trader.getTraderId().equals("InvalidTrader");
    }

    public boolean isAuthorizedToTradeOnDate(Trader trader, LocalDate date) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return !trader.getTraderId().equals("NotAuthorizedToTrade");

    }

    public boolean traderMarginBalanceAvailableForTrade(Trader trader, Integer quantity, Security security) {

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return !security.getSymbol().equals("NoMargin");

    }

    public boolean tradersVaRExposurePermissible(Trader trader) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return !trader.getAccount().getAccountNumber().equals("InvalidVaR");
    }

    public boolean tradersVoEExposurePermissible(Trader trader) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return !trader.getAccount().getAccountNumber().equals("InvalidVoE");
    }

    public boolean tradersDailyLimitNotExceeded(Trader trader, int quantity, Security security) {

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return !trader.getAccount().getAccountNumber().equals("InvalidDailyLimit");
    }

    public boolean orgPermittedToTradeSecurity(Security security) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return !security.getSymbol().equals("---");
    }


}
