package org.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.example.person.Person;
import org.example.portfolio.Portfolio;
import org.example.portfolio.PortfolioGenerator;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ApplicationModule());
        PortfolioGenerator portfolioGenerator = injector.getInstance(PortfolioGenerator.class);

        portfolioGenerator.generate(new Person("Rahul", "Shukla"), new Portfolio("Rahul's portfolio"));
    }
}
