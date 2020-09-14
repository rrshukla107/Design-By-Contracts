package org.example;

public class PortfolioGenerator {

    @UnderValidation
    @ValidateMultipleArgs({"compoundValidation(person, portfolio)", "compoundValidationSecondary(person, portfolio)"})
    public void generate(@ValidateArg({"personValidator", "secondaryPersonValidator"}) Person person, @ValidateArg("portfolioValidator") Portfolio portfolio) {
        System.out.println("Portfolio Generator Called..!");
    }


}
