package org.rahul.dbc.portfolio;

import org.rahul.dbc.annotations.UnderValidation;
import org.rahul.dbc.annotations.ValidateArg;
import org.rahul.dbc.annotations.ValidateMultipleArgs;
import org.rahul.dbc.person.Person;

public class PortfolioGeneratorImpl1 implements PortfolioGenerator {

    @UnderValidation
    @ValidateMultipleArgs({"compoundValidation(person, portfolio)", "compoundValidationSecondary(person, portfolio)"})
    public void generate(@ValidateArg({"personValidator", "secondaryPersonValidator"}) Person person, @ValidateArg("portfolioValidator") Portfolio portfolio) {
        System.out.println("Portfolio Generator Called..!");
    }


}
