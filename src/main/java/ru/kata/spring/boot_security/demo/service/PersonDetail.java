package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Person;

public interface PersonDetail {
    Person findByUsername(String username);
}
