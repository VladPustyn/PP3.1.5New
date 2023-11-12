package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Person;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.PersonRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PersonServiceImpl(PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Person findByUsername(String username) {
        return personRepository.findByUsername(username);
    }

    @Override
    public Person findById(int id) {
        Optional<Person> personOptional = personRepository.findById(id);
        Person person = null;
        if (personOptional.isPresent()) {
            person = personOptional.get();
        }
        return person;
    }

    @Override
    public List<Person> listPerson() {
        return personRepository.findAll();
    }

    @Override
    @Transactional
    public void savePersonWithRoles(Person person, Set<Role> roles) {
        String encodedPassword = passwordEncoder.encode(person.getPassword());
        person.setPassword(encodedPassword);
        Person savedPerson = personRepository.save(person);
        savedPerson.setRoles(roles);
        personRepository.save(savedPerson);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        personRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void updatePersonWithRoles(Person person, int id, Set<Role> roles) {
        Person oldPerson = personRepository.findById(id).orElse(null);

        String password = person.getPassword();

        if (!person.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoder.encode(password);
            oldPerson.setPassword(encodedPassword);
        }

        if (oldPerson != null) {
            oldPerson.setUsername(person.getUsername());
            oldPerson.setAge(person.getAge());
            oldPerson.setPhoneNumber(person.getPhoneNumber());
            oldPerson.setRoles(roles);
            personRepository.save(oldPerson);
        }

    }

}
