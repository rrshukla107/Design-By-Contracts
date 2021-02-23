package org.rahul.dbc.validator.hierarchy;

import org.rahul.dbc.contract.flatcontract.BiFlatContract;
import org.rahul.dbc.person.Person;
import org.rahul.dbc.portfolio.Portfolio;
import org.rahul.dbc.validator.BiContractFactory;

import java.util.HashMap;
import java.util.Map;

public class PortfolioBiContracts implements BiContractFactory {

    private static final String BI_VALIDATOR_1 = "biValidator1";
    private static final String BI_VALIDATOR_2 = "biValidator2";
    private Map<String, BiFlatContract<?, ?>> biContracts;


    public PortfolioBiContracts() {
        this.biContracts = new HashMap<>();

        this.initContracts();
    }

    private void initContracts() {
        BiFlatContract<Person, Portfolio> contract1 = (person, portfolio) -> {
            System.out.println("++++++++++++++++++++++++++++++++++++++");
            System.out.println(BI_VALIDATOR_1 + "executed successfully");
            System.out.println("++++++++++++++++++++++++++++++++++++++");
            return true;
        };

        BiFlatContract<Portfolio, Person> contract2 = (portfolio, person) -> {
            System.out.println("++++++++++++++++++++++++++++++++++++++");
            System.out.println(BI_VALIDATOR_2 + "executed successfully");
            System.out.println("++++++++++++++++++++++++++++++++++++++");
            return true;
        };

        this.biContracts.putIfAbsent(BI_VALIDATOR_1, contract1);
        this.biContracts.putIfAbsent(BI_VALIDATOR_2, contract2);
    }

    @Override
    public Map<String, BiFlatContract<?, ?>> getContracts() {
        return this.biContracts;
    }
}
