package org.rahul.dbc.portfolio;

import java.util.HashMap;
import java.util.Map;

public class Portfolio {

    private String portfolioName;

    private Map<String, String> map = new HashMap<>();


    public Portfolio(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    @Override
    public String toString() {
        return this.portfolioName;
    }
}
