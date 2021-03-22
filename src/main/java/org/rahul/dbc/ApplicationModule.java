package org.rahul.dbc;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import org.rahul.dbc.annotations.UnderValidation;
import org.rahul.dbc.engine.ContractChainExecutorImpl;
import org.rahul.dbc.engine.ContractExecutionEngineImpl;
import org.rahul.dbc.executor_factories.ExecutorServiceFactory;
import org.rahul.dbc.interceptor.ContractHierarchyInterceptor;
import org.rahul.dbc.portfolio.PortfolioGenerator;
import org.rahul.dbc.portfolio.PortfolioGeneratorImpl2;
import org.rahul.dbc.report.TextReportGenerator;
import org.rahul.dbc.use_case.services.ValidationServices;
import org.rahul.dbc.use_case.trade_processing.EquityTradeProcessor;
import org.rahul.dbc.use_case.trade_processing.NewEquityBuyOrderExecutor;
import org.rahul.dbc.use_case.trade_processing.TradeProcessor;
import org.rahul.dbc.use_case.trading_validations.TradeContracts;
import org.rahul.dbc.use_case.trading_validations.TradeExecutionContracts;
import org.rahul.dbc.use_case.trading_validations.TraderContracts;
import org.rahul.dbc.validator.ValidatorFactory;
import org.rahul.dbc.validator.function.Validators;
import org.rahul.dbc.validator.hierarchy.PortfolioBiContracts;
import org.rahul.dbc.validator.hierarchy.PortfolioContracts;

import java.util.List;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {


        bind(Validators.class).in(Singleton.class);
        bind(PortfolioGenerator.class).to(PortfolioGeneratorImpl2.class);
//        bind(ValidatorFactory.class).in(Singleton.class);

        bind(TradeProcessor.class).to(EquityTradeProcessor.class);
        bind(NewEquityBuyOrderExecutor.class);

//        bindInterceptor(
//                Matchers.any(),
//                Matchers.annotatedWith(UnderValidation.class),
//                new ContractsAsFunctionsInterceptor(new Validators()));

        bindInterceptor(
                Matchers.any(),
                Matchers.annotatedWith(UnderValidation.class),
                new ContractHierarchyInterceptor(new ValidatorFactory(List.of(new PortfolioContracts(),
                        new TradeContracts(new ValidationServices()),
                        new TraderContracts(new ValidationServices())),
                        List.of(new PortfolioBiContracts(), new TradeExecutionContracts(new ValidationServices()))),
                        new ContractChainExecutorImpl(new ContractExecutionEngineImpl(ExecutorServiceFactory.getFixedThreadPoolExecutorService())), new TextReportGenerator()));

    }
}
