package org.rahul.dbc;

import net.lamberto.junit.GuiceJUnitRunner;
import net.lamberto.junit.GuiceJUnitRunner.GuiceModules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rahul.dbc.contract.flatcontract.FlatContract;
import org.rahul.dbc.contract.flatcontract.SingleArgCachedFlatContract;
import org.rahul.dbc.contract.impersonator.SingleArgLambdaImpersonator;
import org.rahul.dbc.person.Person;
import org.rahul.dbc.portfolio.Portfolio;
import org.rahul.dbc.portfolio.PortfolioGenerator;
import org.rahul.dbc.validator.hierarchy.PersonValidator;
import org.rahul.dbc.validator.hierarchy.PersonValidatorImpersonator1;
import org.rahul.dbc.validator.hierarchy.PersonValidatorImpersonator2;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@RunWith(GuiceJUnitRunner.class)
@GuiceModules(ApplicationModule.class)
public class ContractsDemoTest {

    @Inject
    PortfolioGenerator portfolioGenerator;

    @Test
    public void demoInterceptionOfContracts() {
        this.portfolioGenerator.generate(new Person("Rahul", "Shukla"), new Portfolio("Rahul's portfolio"));
    }

    @Test
    public void demoCachedHierarchy() {

        FlatContract<Person> contractDelayWith5Seconds = person -> {
            try {
                System.out.println("Validating...");
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return true;
        };

        SingleArgCachedFlatContract<Person> cachedContract = new SingleArgCachedFlatContract<>(contractDelayWith5Seconds);

        Person person = new Person("R", "S");
        System.out.println(cachedContract.validate(person));
        System.out.println(cachedContract.validate(person));

    }

    @Test
    public void demoAndHierarchy() {

    }

    @Test
    public void demoChaperoneContracthierarchy() {

    }

    @Test
    public void demoImpersonatorContractHierarchy() {
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
                })).validate(new Person("Rahul", "Shukla"));


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
