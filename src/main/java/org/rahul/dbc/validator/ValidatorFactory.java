package org.rahul.dbc.validator;

import org.rahul.dbc.contract.flatcontract.FlatContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ValidatorFactory {


    private final Map<String, FlatContract<?>> validationFactory;
    private List<ContractFactory> factories;

    public ValidatorFactory(List<ContractFactory> factories) {
        this.factories = factories;
        this.validationFactory = new HashMap<>();
        this.init();
    }

    private void init() {
        factories.stream()
                .flatMap(factory -> factory
                        .getContracts()
                        .entrySet()
                        .stream())
                .forEach(entry -> this.validationFactory.put(entry.getKey(), entry.getValue()));
    }

    public Optional<FlatContract<?>> getValidator(String name) {
        return Optional.ofNullable(this.validationFactory.getOrDefault(name, null));
    }

}
