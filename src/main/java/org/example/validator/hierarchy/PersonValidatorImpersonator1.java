package org.example.validator.hierarchy;

import org.example.contract.AbstractImpersonator;
import org.example.contract.FlatContract;
import org.example.person.Person;

public class PersonValidatorImpersonator1 extends AbstractImpersonator<Person> {

    public PersonValidatorImpersonator1(FlatContract<Person> contract) {
        super(contract);
    }

    @Override
    public Person preValidation(Person data) {
        System.out.println("PersonValidatorImpersonator1 with person ::" + data);
        return new Person("New1", "Name1");
    }
}
