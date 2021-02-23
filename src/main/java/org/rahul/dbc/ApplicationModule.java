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

//        bindInterceptor(
//                Matchers.any(),
//                Matchers.annotatedWith(UnderValidation.class),
//                new ContractsAsFunctionsInterceptor(new Validators()));

        bindInterceptor(
                Matchers.any(),
                Matchers.annotatedWith(UnderValidation.class),
                new ContractHierarchyInterceptor(new ValidatorFactory(List.of(new PortfolioContracts()), List.of(new PortfolioBiContracts())),
                        new ContractChainExecutorImpl(new ContractExecutionEngineImpl(ExecutorServiceFactory.getFixedThreadPoolExecutorService()))));

    }
}
