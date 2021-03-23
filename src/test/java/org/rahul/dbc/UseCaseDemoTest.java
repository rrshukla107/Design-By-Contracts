package org.rahul.dbc;

import net.lamberto.junit.GuiceJUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rahul.dbc.use_case.account.Account;
import org.rahul.dbc.use_case.trade.Security;
import org.rahul.dbc.use_case.trade.Trade;
import org.rahul.dbc.use_case.trade_processing.NewEquityBuyOrderExecutor;
import org.rahul.dbc.use_case.trade_processing.TraditionalEquityBuyOrderExecutor;
import org.rahul.dbc.use_case.trader.Trader;
import org.rahul.dbc.use_case.trader.TraderType;

import javax.inject.Inject;
import java.time.LocalDate;

@RunWith(GuiceJUnitRunner.class)
@GuiceJUnitRunner.GuiceModules(ApplicationModule.class)
public class UseCaseDemoTest {

    public static final Double MILLIS = 1000000d;

    @Inject
    TraditionalEquityBuyOrderExecutor traditionalEquityBuyOrderExecutor;

    @Inject
    NewEquityBuyOrderExecutor newEquityBuyOrderExecutor;

    @Test
    public void executeTradeUsingTraditionalMethod() throws Exception {


        long startTime = System.nanoTime();
        this.traditionalEquityBuyOrderExecutor.executeOrder(this.getSampleTrade(this.getTrader()), this.getTrader());
        long endTime = System.nanoTime();

        System.out.println("Trade Executed Successfully");
        System.out.println("Time required for execution - " + (endTime - startTime) / MILLIS + " ms");

    }

    @Test
    public void executeTradeUsingContracts() throws Exception {

        this.newEquityBuyOrderExecutor.executeOrder(this.getSampleTrade(this.getTrader()), this.getTrader());

    }

    @Test
    public void executeTradeWithError_TraditionalMethod() {
        long startTime = System.nanoTime();
        try {
            Trade sampleTrade = this.getSampleTrade(this.getTrader());
            sampleTrade.setTradeDate(LocalDate.of(2021, 3, 21));
            this.traditionalEquityBuyOrderExecutor.executeOrder(sampleTrade, this.getTrader());
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            long endTime = System.nanoTime();
            System.out.println("Time required for execution - " + (endTime - startTime) / MILLIS + " ms");
        }

    }

    @Test
    public void executeTradeWithError_WithContracts() throws Exception {
        Trade sampleTrade = this.getSampleTrade(this.getTrader());
        sampleTrade.setTradeDate(LocalDate.of(2021, 3, 21));
        this.newEquityBuyOrderExecutor.executeOrder(sampleTrade, this.getTrader());

    }

    @Test
    public void executeTradeWithMultipleError_WithContracts() throws Exception {
        Trade sampleTrade = this.getSampleTrade(this.getTrader());
        sampleTrade.setTradeDate(LocalDate.of(2021, 3, 21));
        sampleTrade.setSecurity(new Security("---", "Invalid Security"));
        this.newEquityBuyOrderExecutor.executeOrder(sampleTrade, this.getTrader());

    }


    private Trade getSampleTrade(Trader trader) {
        Trade trade = new Trade();
        trade.setSecurity(new Security("GOOG", "Alphabet"));
        trade.setTrader(trader);
        trade.setTradeDate(LocalDate.of(2021, 3, 23));
        trade.setQuantity(100);
        trade.setAskPrice(102d);
        trade.setBidPrice(104d);
        trade.setValueDate(LocalDate.of(2021, 3, 24));
        return trade;
    }

    private Trader getTrader() {
        Trader trader = new Trader();
        trader.setTraderId("trader");
        trader.setAccount(new Account("123456"));
        trader.setTraderType(TraderType.ASSOCIATE);
        return trader;
    }
}
