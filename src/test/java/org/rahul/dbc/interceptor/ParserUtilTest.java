package org.rahul.dbc.interceptor;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rahul.dbc.contract.flatcontract.BiFlatContract;
import org.rahul.dbc.contract.flatcontract.FlatContract;
import org.rahul.dbc.engine.SingleArgContractWrapper;
import org.rahul.dbc.validator.BiContractFactory;
import org.rahul.dbc.validator.ContractFactory;
import org.rahul.dbc.validator.ValidatorFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserUtilTest {


    private ParserUtil parserUtil;
    private Map<String, Object> parameterMappings;
    private ValidatorFactory validatorFactory;


    @Before
    public void setup() {
        this.parserUtil = new ParserUtil();
        this.initParameterMappings();
        this.initValidatorFactory();
    }

    private void initValidatorFactory() {
        ContractFactory contractFactory = () -> {

            Map<String, FlatContract<?>> invariantMap = new HashMap<>();

            invariantMap.put("invariant1", arg -> true);
            invariantMap.put("invariant2", arg -> true);

            return invariantMap;
        };

        BiContractFactory biContractFactory = () -> {

            Map<String, BiFlatContract<?, ?>> biInvariantMap = new HashMap<>();

            biInvariantMap.put("biInvariant1", (arg1, arg2) -> true);
            biInvariantMap.put("biInvariant2", (arg1, arg2) -> true);

            return biInvariantMap;
        };

        this.validatorFactory = new ValidatorFactory(List.of(contractFactory), List.of(biContractFactory));
    }

    private void initParameterMappings() {
        this.parameterMappings = new HashMap<>();
        this.parameterMappings.put("arg1", "arg1_value");
        this.parameterMappings.put("arg2", 2L);
        this.parameterMappings.put("arg3", new Object());
    }

    // single-arg contracts
    // chain_name = invariant1(argument) -> invariant2(argument)

    @Test
    public void whenSingleArgChain_parseChain() {

        String[] invariants = {"chain1 = invariant1(arg1) -> invariant2(arg2)"};

        Map<String, List<SingleArgContractWrapper<?>>> contractWrapper = ParserUtil.getContractWrapper(invariants, this.parameterMappings, this.validatorFactory);
        Assert.assertEquals(contractWrapper.size(), 1L);
        Assert.assertEquals(contractWrapper.get("chain1").size(), 2L);

    }

    @Test
    public void whileBiArgChain_ParseChain() {

        String[] invariants = {"biChain1 = biInvariant1(arg1, arg2) -> byInvariant2(arg2, ARG3)"};


    }

}