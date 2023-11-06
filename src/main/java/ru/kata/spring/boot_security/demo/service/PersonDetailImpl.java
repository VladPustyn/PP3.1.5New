package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.Person;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.PersonRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class PersonDetailImpl implements UserDetailsService, PersonDetail {

    private PersonRepository personRepository;

    @Autowired
    public PersonDetailImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }


    @Override
    public Person findByUsername(String username) {
        return personRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = findByUsername(username);

        if (person == null) {
            throw new UsernameNotFoundException("Пользователь с таким именем не найден");
        }

        return new User(person.getUsername(), person.getPassword(),
                mapRolesToAuthorities(person.getRoles()));
    }
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(x -> new SimpleGrantedAuthority(x.getName())).collect(Collectors.toList());
    }

}
