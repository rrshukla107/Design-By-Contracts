package org.example.validator.hierarchy;

import org.example.contract.FlatContract;
import org.example.person.Person;

public class PersonValidator implements FlatContract<Person> {
    @Override
    public boolean validate(Person person) {
        System.out.println("This is PersonValidator with person :: " + person);
        return true;
    }
}
