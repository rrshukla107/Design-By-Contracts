package org.rahul.dbc.use_case.trader;

import lombok.Data;
import org.rahul.dbc.use_case.account.Account;

@Data
public class Trader {

    private String firstName;

    private String lastName;

    private TraderType traderType;

    private Account account;

}
