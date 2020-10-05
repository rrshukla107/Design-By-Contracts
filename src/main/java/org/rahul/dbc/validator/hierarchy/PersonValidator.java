package org.rahul.dbc.validator.hierarchy;

import org.rahul.dbc.contract.flatcontract.FlatContract;
import org.rahul.dbc.person.Person;

public class PersonValidator implements FlatContract<Person> {
    @Override
    public boolean validate(Person person) {
        System.out.println("[[PERSON VALIDATOR 1]] with person :: " + person);
        return true;
    }
}
