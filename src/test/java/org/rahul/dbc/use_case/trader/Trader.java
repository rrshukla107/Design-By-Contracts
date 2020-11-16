package org.rahul.dbc.use_case.trader;

import lombok.Data;
import lombok.Getter;
import org.rahul.dbc.use_case.account.Account;

@Data
@Getter
public class Trader {

    private String traderId;

    private String firstName;

    private String lastName;

    private TraderType traderType;

    private Account account;

}
