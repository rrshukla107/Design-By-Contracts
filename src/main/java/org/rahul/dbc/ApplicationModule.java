package org.rahul.dbc;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matchers;
import org.rahul.dbc.annotations.UnderValidation;
import org.rahul.dbc.annotations.Validate;
import org.rahul.dbc.interceptor.ContractHierarchyInterceptor;
import org.rahul.dbc.interceptor.ContractsAsFunctionsInterceptor;
import org.rahul.dbc.portfolio.PortfolioGenerator;
import org.rahul.dbc.portfolio.PortfolioGeneratorImpl1;
import org.rahul.dbc.validator.function.Validators;
import org.rahul.dbc.validator.hierarchy.ValidatorFactory;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(Validators.class).in(Singleton.class);
        bind(PortfolioGenerator.class).to(PortfolioGeneratorImpl1.class);
//        bind(ValidatorFactory.class).in(Singleton.class);

        bindInterceptor(
                Matchers.any(),
                Matchers.annotatedWith(UnderValidation.class),
                new ContractsAsFunctionsInterceptor(new Validators()));

        bindInterceptor(
                Matchers.any(),
                Matchers.annotatedWith(Validate.class),
                new ContractHierarchyInterceptor(new ValidatorFactory()));

    }
}
