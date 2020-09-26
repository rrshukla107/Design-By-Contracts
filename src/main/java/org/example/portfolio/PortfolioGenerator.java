package org.example.portfolio;

import org.example.annotations.UnderValidation;
import org.example.annotations.ValidateArg;
import org.example.annotations.ValidateMultipleArgs;
import org.example.person.Person;

public class PortfolioGenerator {

    @UnderValidation
    @ValidateMultipleArgs({"compoundValidation(person, portfolio)", "compoundValidationSecondary(person, portfolio)"})
    public void generate(@ValidateArg({"personValidator", "secondaryPersonValidator"}) Person person, @ValidateArg("portfolioValidator") Portfolio portfolio) {
        System.out.println("Portfolio Generator Called..!");
    }


}
