package org.example;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.example.contract.SingleArgLambdaImpersonator;
import org.example.person.Person;
import org.example.portfolio.PortfolioGeneratorImpl1;
import org.example.validator.hierarchy.PersonValidator;
import org.example.validator.hierarchy.PersonValidatorImpersonator1;
import org.example.validator.hierarchy.PersonValidatorImpersonator2;

public class Main {

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ApplicationModule());
        PortfolioGeneratorImpl1 portfolioGenerator = injector.getInstance(PortfolioGeneratorImpl1.class);

//        portfolioGenerator.generate(new Person("Rahul", "Shukla"), new Portfolio("Rahul's portfolio"));

        testImpersonator();
    }

    private static void testImpersonator() {
        //Method 1
        System.out.println("--------METHOD 1------------");
        new PersonValidatorImpersonator1(new PersonValidatorImpersonator2(new PersonValidator())).validate(new Person("Rahul", "Shukla"));

        //Method 2
        System.out.println("--------METHOD 2------------");
        new SingleArgLambdaImpersonator<>(new SingleArgLambdaImpersonator<Person>(person -> {
            System.out.println("This is the original Validator");
            System.out.println("Person :: " + person);
            return true;
        }, person -> {
            Person p = new Person("Name 1", "Surname 1");
            System.out.println("Person input :: " + person);
            System.out.println("Person changed to ::" + p);
            return p;
        }), person -> {
            Person p = new Person("Name 2", "Surname 2");
            System.out.println("Person input :: " + person);
            System.out.println("Person changed to ::" + p);
            return p;
        }).validate(new Person("Rahul", "Shukla"));

        //Method 3
        System.out.println("--------METHOD 3------------");
        new SingleArgLambdaImpersonator<>(new PersonValidatorImpersonator1(new PersonValidator()), person -> {
            Person p = new Person("Name 2", "Surname 2");
            System.out.println("Person input :: " + person);
            System.out.println("Person changed to ::" + p);
            return p;
        }).validate(new Person("Rahul", "Shukla"));
    }
}
