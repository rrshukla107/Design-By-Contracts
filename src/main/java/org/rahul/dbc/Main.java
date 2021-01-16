package org.rahul.dbc;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.rahul.dbc.contract.impersonator.SingleArgLambdaImpersonator;
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
        portfolioGenerator.generate(new Person("Rahul", "Shukla"), new Portfolio("Rahul's portfolio"));


        System.out.println("Executed successfully");
//        testImpersonator();
    }

    private static void testImpersonator() {
        //Method 1
        System.out.println("--------METHOD 1------------");
        new PersonValidatorImpersonator1(new PersonValidatorImpersonator2(new PersonValidator())).validate(new Person("Rahul", "Shukla"));

        //Method 2
        System.out.println("--------METHOD 2------------");

        new SingleArgLambdaImpersonator<>(person -> {
            Person p = new Person("Name 1", "Surname 1");
            System.out.println("Person input :: " + person);
            System.out.println("Person changed to ::" + p);
            return p;
        },
                new SingleArgLambdaImpersonator<>(person -> {
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
        new SingleArgLambdaImpersonator<>(person -> {
            Person p = new Person("Name 2", "Surname 2");
            System.out.println("Person input :: " + person);
            System.out.println("Person changed to ::" + p);
            return p;
        }, new PersonValidatorImpersonator1(new PersonValidator())).validate(new Person("Rahul", "Shukla"));
    }
}
