package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class Controller {

    @GetMapping("/lol")
    public String page() {
        return "home";
    }

@GetMapping("/user")
    public String pageUser(Principal principal) {
        return "USER page " + principal.getName();
}

    @GetMapping("/admin")
    public String pageAdmin(Principal principal) {
        return "ADMIN page " + principal.getName();
    }
}
