package org.rahul.dbc.validator;

import org.rahul.dbc.contract.flatcontract.BiFlatContract;
import org.rahul.dbc.contract.flatcontract.FlatContract;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ValidatorFactory {


    private final Map<String, FlatContract<?>> validationFactory;
    private final Map<String, BiFlatContract<?, ?>> biValidationFactory;

    private final List<ContractFactory> contractFactories;
    private final List<BiContractFactory> biContractFactories;

    public ValidatorFactory(List<ContractFactory> contractFactories, List<BiContractFactory> biContractFactories) {
        this.contractFactories = contractFactories;
        this.biContractFactories = biContractFactories;
        this.validationFactory = new HashMap<>();
        this.biValidationFactory = new HashMap<>();

        this.init();
    }

    private void init() {
        this.contractFactories.stream()
                .flatMap(factory -> factory
                        .getContracts()
                        .entrySet()
                        .stream())
                .forEach(entry -> this.validationFactory.put(entry.getKey(), entry.getValue()));

        this.biContractFactories.stream()
                .flatMap(factory -> factory
                        .getContracts()
                        .entrySet()
                        .stream())
                .forEach(entry -> this.biValidationFactory.put(entry.getKey(), entry.getValue()));
    }

    public Optional<FlatContract<?>> getValidator(String name) {
        return Optional.ofNullable(this.validationFactory.getOrDefault(name, null));
    }

    public Optional<BiFlatContract<?, ?>> getBiValidator(String name) {
        return Optional.ofNullable((this.biValidationFactory.getOrDefault(name, null)));
    }

}
