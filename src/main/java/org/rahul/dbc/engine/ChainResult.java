package org.rahul.dbc.engine;

import java.util.Map;
import java.util.Optional;

public interface ChainResult {

    boolean getResult();

    boolean hasFailed();

    boolean isSuccessful();

    Optional<Throwable> getUnderlyingException();

    Optional<String> getFailedContractName();

    Map<String, Double> getExecutionTimes();

    void setExecutionTimes(final Map<String, Double> result);

}
