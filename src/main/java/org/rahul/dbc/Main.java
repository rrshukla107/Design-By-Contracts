package org.rahul.dbc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.rahul.dbc.contract.impersonator.ImpersonatorLambda;
import org.rahul.dbc.executor_factories.ExecutorServiceFactory;
import org.rahul.dbc.person.Person;
import org.rahul.dbc.portfolio.Portfolio;
import org.rahul.dbc.portfolio.PortfolioGenerator;
import org.rahul.dbc.portfolio.PortfolioGeneratorImpl2;
import org.rahul.dbc.validator.hierarchy.PersonValidator;
import org.rahul.dbc.validator.hierarchy.PersonValidatorImpersonator1;
import org.rahul.dbc.validator.hierarchy.PersonValidatorImpersonator2;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ApplicationModule());
//        PortfolioGenerator portfolioGenerator = injector.getInstance(PortfolioGeneratorImpl1.class);
        PortfolioGenerator portfolioGenerator = injector.getInstance(PortfolioGeneratorImpl2.class);


        System.out.println("Executed successfully");
        try {
            portfolioGenerator.generate(new Person("Rahul", "Shukla"), new Portfolio("Rahul's portfolio"));
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            try {
                ExecutorServiceFactory.shutDownExecutorService();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

//        testImpersonator();
    }

    private static void testImpersonator() {
        //Method 1
        System.out.println("--------METHOD 1------------");
        new PersonValidatorImpersonator1(new PersonValidatorImpersonator2(new PersonValidator())).validate(new Person("Rahul", "Shukla"));

        //Method 2
        System.out.println("--------METHOD 2------------");

        new ImpersonatorLambda<>(person -> {
            Person p = new Person("Name 1", "Surname 1");
            System.out.println("Person input :: " + person);
            System.out.println("Person changed to ::" + p);
            return p;
        },
                new ImpersonatorLambda<>(person -> {
                    Person p = new Person("Name 2", "Surname 2");
                    System.out.println("Person input :: " + person);
                    System.out.println("Person changed to ::" + p);
                    return p;
                }, person -> {
                    System.out.println("This is PersonValidator with person :: " + person);
                    return true;
                }));


        //Method 3
        System.out.println("--------METHOD 3------------");
        new ImpersonatorLambda<>(person -> {
            Person p = new Person("Name 2", "Surname 2");
            System.out.println("Person input :: " + person);
            System.out.println("Person changed to ::" + p);
            return p;
        }, new PersonValidatorImpersonator1(new PersonValidator())).validate(new Person("Rahul", "Shukla"));
    }
}
