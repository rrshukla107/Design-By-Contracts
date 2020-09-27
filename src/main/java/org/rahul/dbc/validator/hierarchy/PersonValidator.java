package org.rahul.dbc.validator.hierarchy;

import org.rahul.dbc.contract.FlatContract;
import org.rahul.dbc.person.Person;

public class PersonValidator implements FlatContract<Person> {
    @Override
    public boolean validate(Person person) {
        System.out.println("[[PORTFOLIO VALIDATOR 1]] with person :: " + person);
        return true;
    }
}
