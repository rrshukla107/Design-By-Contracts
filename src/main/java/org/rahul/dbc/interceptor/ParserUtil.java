package org.rahul.dbc.interceptor;

import org.rahul.dbc.engine.BiContractWrapper;
import org.rahul.dbc.engine.SingleArgContractWrapper;
import org.rahul.dbc.validator.ValidatorFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParserUtil {

    public static Map<String, List<BiContractWrapper<?, ?>>> getBiArgContractWrapper(final String[] invariants, final Map<String, Object> parameterMappings, final ValidatorFactory validatorFactory) {


        Map<String, List<BiContractWrapper<?, ?>>> contractChainMappings = new HashMap<>();

        for (String invariant : invariants) {
            String[] chainDefinition = invariant.split("=");
            String chainName = chainDefinition[0].strip();
            String[] contractDetails = chainDefinition[1].strip().split("->");

            List<BiContractWrapper<?, ?>> contractWrappers = new ArrayList<>();

            for (String contract : contractDetails) {
                String strippedContract = contract.strip();
                String contractName = strippedContract.substring(0, strippedContract.indexOf("(")).strip();
                String[] args = strippedContract
                        .substring(strippedContract.indexOf("(") + 1, strippedContract.indexOf(")"))
                        .strip()
                        .split(",");

                validatorFactory.getBiValidator(contractName)
                        .map(v -> new BiContractWrapper(contractName, v, parameterMappings.get(args[0].strip()), parameterMappings.get(args[1].strip())))
                        .ifPresent(contractWrappers::add);
            }

            contractChainMappings.put(chainName, contractWrappers);
        }


        return contractChainMappings;
    }


    public static Map<String, List<SingleArgContractWrapper<?>>> getContractWrapper(final String[] invariants, final Map<String, Object> parameterMappings, final ValidatorFactory validatorFactory) {

        Map<String, List<SingleArgContractWrapper<?>>> contractChainMappings = new HashMap<>();

        for (String invariant : invariants) {
            String[] chainDefinition = invariant.split("=");
            String chainName = chainDefinition[0].strip();
            String[] contractDetails = chainDefinition[1].strip().split("->");

            List<SingleArgContractWrapper<?>> contractWrappers = new ArrayList<>();

            for (String contract : contractDetails) {
                String strippedContract = contract.strip();
                String contractName = strippedContract.substring(0, strippedContract.indexOf("(")).strip();
                String[] args = strippedContract
                        .substring(strippedContract.indexOf("(") + 1, strippedContract.indexOf(")"))
                        .strip()
                        .split(",");

                validatorFactory.getValidator(contractName)
                        .map(v -> new SingleArgContractWrapper(contractName, v, parameterMappings.get(args[0].strip())))
                        .ifPresent(contractWrappers::add);
            }

            contractChainMappings.put(chainName, contractWrappers);
        }


        return contractChainMappings;
    }

}
