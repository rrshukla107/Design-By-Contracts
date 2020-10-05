package org.rahul.dbc.validator.hierarchy;

import org.rahul.dbc.contract.flatcontract.FlatContract;
import org.rahul.dbc.contract.impersonator.SingleArgLambdaImpersonator;
import org.rahul.dbc.person.Person;
import org.rahul.dbc.portfolio.Portfolio;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ValidatorFactory {

    public static final String PERSON_VALIDATOR1 = "personValidator1";
    public static final String PERSON_VALIDATOR2 = "personValidator2";
    public static final String PORTFOLIO_VALIDATOR1 = "portfolioValidator1";
    public static final String PORTFOLIO_VALIDATOR2 = "portfolioValidator2";


    private final Map<String, FlatContract<?>> validationFactory;

    public ValidatorFactory() {
        this.validationFactory = new HashMap<>();
        this.init();
    }

    private void init() {
        FlatContract<Person> personValidator2 = person -> {
            System.out.println("[[PERSON VALIDATOR 2]] person " + person);
            return true;
        };

        FlatContract<Portfolio> portfolioValidator1 = portfolio -> {
            System.out.println("[[PORTFOLIO VALIDATOR 1]] portfolio " + portfolio);
            return true;
        };

        FlatContract<Portfolio> portfolioValidator2 = new SingleArgLambdaImpersonator<>(
                portfolio ->
                        new Portfolio(portfolio + "Imp1"),
                portfolio -> {
                    System.out.println("[[PORTFOLIO VALIDATOR 2]] portfolio" + portfolio);
                    return true;
                });


        this.validationFactory.put(PERSON_VALIDATOR1, new PersonValidatorImpersonator2(new PersonValidatorImpersonator1(new PersonValidator())));
        this.validationFactory.put(PERSON_VALIDATOR2, personValidator2);
        this.validationFactory.put(PORTFOLIO_VALIDATOR1, portfolioValidator1);
        this.validationFactory.put(PORTFOLIO_VALIDATOR2, portfolioValidator2);
    }

    public Optional<FlatContract<?>> getValidator(String name) {
        return Optional.ofNullable(this.validationFactory.getOrDefault(name, null));
    }

}
