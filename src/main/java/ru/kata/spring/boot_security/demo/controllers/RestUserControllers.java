package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.model.Person;
import ru.kata.spring.boot_security.demo.service.PersonService;

import java.security.Principal;

@RestController
@RequestMapping("/user")
public class RestUserControllers {
    @Autowired
    PersonService personService;

    @GetMapping("/info")
    public ResponseEntity<Person> personInfo(Principal principal) {
        Person person = personService.findByUsername(principal.getName());
        return new ResponseEntity<>(person, HttpStatus.OK);
    }
}
