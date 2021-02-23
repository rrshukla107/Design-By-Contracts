package org.rahul.dbc.portfolio;

import org.rahul.dbc.annotations.BiValidate;
import org.rahul.dbc.annotations.PostValidate;
import org.rahul.dbc.annotations.UnderValidation;
import org.rahul.dbc.annotations.Validate;
import org.rahul.dbc.person.Person;

public class PortfolioGeneratorImpl2 implements PortfolioGenerator {

    @Override
    @UnderValidation
    @BiValidate(value = {"bi-arg-chain1 = biValidator1(person, portfolio) -> biValidator2(portfolio, person)"})
    @PostValidate(value = {"post-arg-chain1 = numberValidator1(*)", "post-arg-chain2 = numberValidator2(*)"})
    @Validate(value = {" single-arg-chain1 = personValidator1(person) -> personValidator2(person)",
            "single-arg-chain2 = portfolioValidator(portfolio) -> portfolioValidator2(portfolio)"})
    public int generate(Person person, Portfolio portfolio) {
        System.out.println("Portfolio Generated ");
        return 1;
    }
}
