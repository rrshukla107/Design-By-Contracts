package org.example.validator.hierarchy;

import org.example.contract.AbstractImpersonator;
import org.example.contract.FlatContract;
import org.example.person.Person;

public class PersonValidatorImpersonator2 extends AbstractImpersonator<Person> {

    public PersonValidatorImpersonator2(FlatContract<Person> contract) {
        super(contract);
    }

    @Override
    public Person preValidation(Person data) {
        System.out.println("PersonValidatorImpersonator2 with person ::" + data);
        return new Person("New2", "Name2");
    }
}
