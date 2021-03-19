package org.rahul.dbc.use_case.trade;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TradeConfirmation {

    private Integer totalSharesBought;

    private Double pricePerShare;

    private LocalDate tradeExecutionDate;

    private LocalDate settlementDate;

    private Double commissionPaid;

}
