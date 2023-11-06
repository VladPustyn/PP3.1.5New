package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Person;
import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;
import java.util.Set;

public interface PersonService {
    Person findByUsername(String username);

    List<Person> listPerson();

     void savePersonWithRoles(Person person, Set<Role> roles);

    void deleteById(int id);

     Person findById(int id);

     void updatePersonWithRoles(Person person, int id, Set<Role> roles);
}
