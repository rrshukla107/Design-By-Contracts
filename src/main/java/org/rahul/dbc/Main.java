package org.rahul.dbc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.rahul.dbc.executor_factories.ExecutorServiceFactory;
import org.rahul.dbc.use_case.account.Account;
import org.rahul.dbc.use_case.trade.Security;
import org.rahul.dbc.use_case.trade.Trade;
import org.rahul.dbc.use_case.trade_processing.NewEquityBuyOrderExecutor;
import org.rahul.dbc.use_case.trade_processing.TraditionalEquityBuyOrderExecutor;
import org.rahul.dbc.use_case.trader.Trader;
import org.rahul.dbc.use_case.trader.TraderType;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ApplicationModule());


        try {
            startApplication(injector);
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            try {
                ExecutorServiceFactory.shutDownExecutorService();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private static void startApplication(Injector injector) throws Exception {
//        PortfolioGenerator portfolioGenerator = injector.getInstance(PortfolioGeneratorImpl2.class);
//        portfolioGenerator.generate(new Person("Rahul", "Shukla"), new Portfolio("Rahul's portfolio"));
//
        NewEquityBuyOrderExecutor orderExecutor = injector.getInstance(NewEquityBuyOrderExecutor.class);
        TraditionalEquityBuyOrderExecutor traditionalEquityBuyOrderExecutor = injector.getInstance((TraditionalEquityBuyOrderExecutor.class));

        Trader trader = getTrader();

        Trade trade = getSampleTrade(trader);

        traditionalEquityBuyOrderExecutor.executeOrder(trade, trader);

//        orderExecutor.executeOrder(trade, trader);
    }

    private static Trade getSampleTrade(Trader trader) {
        Trade trade = new Trade();
        trade.setSecurity(new Security("GOOG", "Alphabet"));
        trade.setTrader(trader);
        trade.setTradeDate(LocalDate.of(2021, 3, 23));
        trade.setQuantity(100);
        trade.setValueDate(LocalDate.of(2021, 3, 24));
        return trade;
    }

    private static Trader getTrader() {
        Trader trader = new Trader();
        trader.setTraderId("trader");
        trader.setAccount(new Account("123456"));
        trader.setTraderType(TraderType.ASSOCIATE);
        return trader;
    }

}
