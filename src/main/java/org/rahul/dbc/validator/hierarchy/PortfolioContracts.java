package org.rahul.dbc.validator.hierarchy;

import org.rahul.dbc.contract.flatcontract.FlatContract;
import org.rahul.dbc.contract.impersonator.ImpersonatorLambda;
import org.rahul.dbc.person.Person;
import org.rahul.dbc.portfolio.Portfolio;
import org.rahul.dbc.validator.ContractFactory;

import java.util.HashMap;
import java.util.Map;

public class PortfolioContracts implements ContractFactory {


    public static final String PERSON_VALIDATOR1 = "personValidator1";
    public static final String PERSON_VALIDATOR2 = "personValidator2";
    public static final String PORTFOLIO_VALIDATOR1 = "portfolioValidator1";
    public static final String PORTFOLIO_VALIDATOR2 = "portfolioValidator2";

    private final Map<String, FlatContract<?>> contracts = new HashMap<>();

    public PortfolioContracts() {

        FlatContract<Person> personValidator2 = person -> {
            System.out.println("[[PERSON VALIDATOR 2]] person " + person);
            return true;
        };

        FlatContract<Portfolio> portfolioValidator1 = portfolio -> {
            System.out.println("[[PORTFOLIO VALIDATOR 1]] portfolio " + portfolio);
            return true;
        };

        FlatContract<Portfolio> portfolioValidator2 = new ImpersonatorLambda<>(
                portfolio ->
                        new Portfolio(portfolio + "Imp1"),
                portfolio -> {
                    System.out.println("[[PORTFOLIO VALIDATOR 2]] portfolio" + portfolio);
                    return true;
                });


        this.contracts.put(PERSON_VALIDATOR1, new PersonValidatorImpersonator2(new PersonValidatorImpersonator1(new PersonValidator())));
        this.contracts.put(PERSON_VALIDATOR2, personValidator2);
        this.contracts.put(PORTFOLIO_VALIDATOR1, portfolioValidator1);
        this.contracts.put(PORTFOLIO_VALIDATOR2, portfolioValidator2);
    }


    @Override
    public Map<String, FlatContract<?>> getContracts() {
        return this.contracts;
    }
}
