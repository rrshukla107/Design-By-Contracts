package org.example;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class ApplicationModule extends AbstractModule {
    @Override
    protected void configure() {

        bind(Validators.class);
        bind(PortfolioGenerator.class);

        bindInterceptor(
                Matchers.any(),
                Matchers.annotatedWith(UnderValidation.class),
                new Interceptor(new Validators()));

    }
}
