package org.rahul.dbc.validator.hierarchy;

import org.rahul.dbc.contract.flatcontract.FlatContract;
import org.rahul.dbc.contract.impersonator.AbstractImpersonator;
import org.rahul.dbc.person.Person;

public class PersonValidatorImpersonator1 extends AbstractImpersonator<Person> {

    public PersonValidatorImpersonator1(FlatContract<Person> contract) {
        super(contract);
    }

    @Override
    public Person impersonateArgument(Person data) {
        System.out.println("PersonValidatorImpersonator1 with person ::" + data);
        return new Person("New1", "Name1");
    }
}
