package org.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.example.person.Person;
import org.example.portfolio.Portfolio;
import org.example.portfolio.PortfolioGeneratorImpl1;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ApplicationModule());
        PortfolioGeneratorImpl1 portfolioGenerator = injector.getInstance(PortfolioGeneratorImpl1.class);

        portfolioGenerator.generate(new Person("Rahul", "Shukla"), new Portfolio("Rahul's portfolio"));
    }
}
