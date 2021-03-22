package org.rahul.dbc.engine;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractExecutionResult {

    private Boolean result;

    private Double runTime;

    public ContractExecutionResult(final Boolean result, final Double runTime) {
        this.result = result;
        this.runTime = runTime;
    }

}
