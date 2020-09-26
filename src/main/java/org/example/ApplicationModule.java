package org.example;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import org.example.annotations.UnderValidation;
import org.example.interceptor.Interceptor;
import org.example.portfolio.PortfolioGenerator;
import org.example.portfolio.PortfolioGeneratorImpl1;
import org.example.validator.function.Validators;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(Validators.class);
        bind(PortfolioGenerator.class).to(PortfolioGeneratorImpl1.class);

        bindInterceptor(
                Matchers.any(),
                Matchers.annotatedWith(UnderValidation.class),
                new Interceptor(new Validators()));

    }
}
