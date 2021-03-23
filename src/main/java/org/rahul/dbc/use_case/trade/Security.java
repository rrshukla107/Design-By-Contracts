package org.rahul.dbc.use_case.trade;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Security {

    private String symbol;

    private String name;
}
