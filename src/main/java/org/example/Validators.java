package org.example;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Validators {

    public Map<String, Function> businessValidators = new HashMap<>();

    boolean compoundValidation(Person person, Portfolio portfolio) {
        System.out.println("In compound Validation");
        System.out.println("Person :: " + person);
        System.out.println("Portfolio :: " + portfolio);
        return true;
    }

    boolean personValidator(Person person) {
        System.out.println("person validator called");
        System.out.println("person :: " + person);
        return true;
    }

    boolean portfolioValidator(Portfolio portfolio) {
        System.out.println("portfolio validator called");
        System.out.println("portfolio :: " + portfolio);
        return true;
    }
}
