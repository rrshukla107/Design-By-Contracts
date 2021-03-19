package org.rahul.dbc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.rahul.dbc.executor_factories.ExecutorServiceFactory;
import org.rahul.dbc.person.Person;
import org.rahul.dbc.portfolio.Portfolio;
import org.rahul.dbc.portfolio.PortfolioGenerator;
import org.rahul.dbc.portfolio.PortfolioGeneratorImpl2;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ApplicationModule());
//        PortfolioGenerator portfolioGenerator = injector.getInstance(PortfolioGeneratorImpl1.class);


        System.out.println("Executed successfully");
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

    private static void startApplication(Injector injector) {
        PortfolioGenerator portfolioGenerator = injector.getInstance(PortfolioGeneratorImpl2.class);
        portfolioGenerator.generate(new Person("Rahul", "Shukla"), new Portfolio("Rahul's portfolio"));
    }

}
