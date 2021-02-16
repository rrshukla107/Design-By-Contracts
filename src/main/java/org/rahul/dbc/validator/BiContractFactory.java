package org.rahul.dbc.validator;

import org.rahul.dbc.contract.flatcontract.BiFlatContract;

import java.util.Map;

public interface BiContractFactory {

    Map<String, BiFlatContract<?, ?>> getContracts();
}
