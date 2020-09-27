package org.rahul.dbc.portfolio;

import org.rahul.dbc.annotations.Validate;
import org.rahul.dbc.person.Person;

public class PortfolioGeneratorImpl2 implements PortfolioGenerator {

    @Override
    @Validate(value = {"person = personValidator1, personValidator2",
            "portfolio = portfolioValidator1, portfolioValidator2"})
    public void generate(Person person, Portfolio portfolio) {
        System.out.println("Portfolio " + portfolio + "Generated for " + person);
    }
}
