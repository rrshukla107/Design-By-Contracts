package org.rahul.dbc.validator;

import org.rahul.dbc.contract.flatcontract.FlatContract;

import java.util.Map;

public interface ContractFactory {

    Map<String, FlatContract<?>> getContracts();
}
