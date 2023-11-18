package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Person;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.service.PersonService;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.util.PersonValidator;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class RestAdminControllers {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PersonService personService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private PersonValidator personValidator;


    @GetMapping("/users")
    public ResponseEntity<List<Person>> showAdminInfo() {
        List<Person> persons = personService.listPerson();
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<Person> personInfo(Principal principal) {
        Person person = personService.findByUsername(principal.getName());
        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<Set<String>> personRoles(Principal principal) {
        Person person = personService.findByUsername(principal.getName());
        Set<Role> roles = person.getRoles();
        Set<String> roleNames = roles.stream()
                .map(role -> role.getName().replace("ROLE_", ""))
                .collect(Collectors.toSet());
        return new ResponseEntity<>(roleNames, HttpStatus.OK);
    }


    @PostMapping("/create")
    public ResponseEntity<String> createPersonWithRoles(@RequestBody Person person) {
        Set<Role> roles = person.getRoles();

        System.out.println(person);
        System.out.println(roles.toString());

        personService.savePersonWithRoles(person, roles);
        return ResponseEntity.ok("Person created successfully!");
    }


    @PatchMapping("/update/{id}")
    public ResponseEntity update(@RequestBody Person newPerson,
                                 @PathVariable int id) {

        Set<Role> roles = newPerson.getRoles();
        personService.updatePersonWithRoles(newPerson, id, roles);
        return new ResponseEntity<>("Person deleted successfully", HttpStatus.OK);
    }


    @GetMapping("/role")
    public ResponseEntity<Map<String, String>> adminGetRole(Principal principal) {
        Person person = personService.findByUsername(principal.getName());
        Set<Role> personRole = person.getRoles();

        Map<String, String> roles = new HashMap();
        roles.put("username", person.getUsername());
        roles.put("admin", "ADMIN");

        boolean hasUserRole = personRole.stream().anyMatch(role -> role.getName().equals("ROLE_USER"));
        if (hasUserRole) {
            roles.put("user", "USER");
        }

        return new ResponseEntity<>(roles, HttpStatus.OK);
    }


    @GetMapping("/userInfo/{id}")
    public ResponseEntity personInfo(@PathVariable int id) {
        Person person = personService.findById(id);
        return new ResponseEntity<>(person, HttpStatus.OK);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity deletePerson(@PathVariable int id) {
        personService.deleteById(id);
        return new ResponseEntity<>("Person deleted successfully", HttpStatus.OK);
    }
}


