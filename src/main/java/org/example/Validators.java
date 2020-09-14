package org.example;

public class Validators {

    boolean compoundValidation(Person person, Portfolio portfolio) {
        System.out.println("In compoundValidation");
        System.out.println("Person :: " + person);
        System.out.println("Portfolio :: " + portfolio);
        return true;
    }

    boolean compoundValidationSecondary(Person person, Portfolio portfolio) {
        System.out.println("In compoundValidationSecondary");
        System.out.println("Person :: " + person);
        System.out.println("Portfolio :: " + portfolio);
        return true;
    }

    boolean personValidator(Person person) {
        System.out.println("person validator called");
        System.out.println("person :: " + person);
        return true;
    }

    boolean secondaryPersonValidator(Person person) {
        System.out.println("secondary person validator called");
        System.out.println("person :: " + person);
        return true;
    }

    boolean portfolioValidator(Portfolio portfolio) {
        System.out.println("portfolio validator called");
        System.out.println("portfolio :: " + portfolio);
        return true;
    }
}
