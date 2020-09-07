package org.example;

public class PortfolioGenerator {

    @UnderValidation
    @ValidateArg("compoundValidation(person, portfolio)")
    public void generate(@ValidateArg("personValidator") Person person, @ValidateArg("portfolioValidator") Portfolio portfolio) {
        System.out.println("generate method called..!");
    }


}
