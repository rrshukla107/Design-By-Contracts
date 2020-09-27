package org.rahul.dbc.validator.function;

import org.rahul.dbc.person.Person;
import org.rahul.dbc.portfolio.Portfolio;

public class Validators {

    public boolean compoundValidation(Person person, Portfolio portfolio) {
        System.out.println("In compoundValidation");
        System.out.println("Person :: " + person);
        System.out.println("Portfolio :: " + portfolio);
        return true;
    }

    public boolean compoundValidationSecondary(Person person, Portfolio portfolio) {
        System.out.println("In compoundValidationSecondary");
        System.out.println("Person :: " + person);
        System.out.println("Portfolio :: " + portfolio);
        return true;
    }

    public boolean personValidator(Person person) {
        System.out.println("person validator called");
        System.out.println("person :: " + person);
        return true;
    }

    public boolean secondaryPersonValidator(Person person) {
        System.out.println("secondary person validator called");
        System.out.println("person :: " + person);
        return true;
    }

    public boolean portfolioValidator(Portfolio portfolio) {
        System.out.println("portfolio validator called");
        System.out.println("portfolio :: " + portfolio);
        return true;
    }
}
