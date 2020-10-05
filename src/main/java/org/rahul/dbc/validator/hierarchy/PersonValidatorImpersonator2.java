package org.rahul.dbc.validator.hierarchy;

import org.rahul.dbc.contract.AbstractImpersonator;
import org.rahul.dbc.contract.FlatContract;
import org.rahul.dbc.person.Person;

public class PersonValidatorImpersonator2 extends AbstractImpersonator<Person> {

    public PersonValidatorImpersonator2(FlatContract<Person> contract) {
        super(contract);
    }

    @Override
    public Person impersonateArgument(Person data) {
        System.out.println("PersonValidatorImpersonator2 with person ::" + data);
        return new Person("New2", "Name2");
    }
}
