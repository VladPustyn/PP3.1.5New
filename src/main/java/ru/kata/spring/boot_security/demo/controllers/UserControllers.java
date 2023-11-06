package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kata.spring.boot_security.demo.model.Person;
import ru.kata.spring.boot_security.demo.service.PersonService;
import ru.kata.spring.boot_security.demo.service.PersonServiceImpl;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserControllers {
    @Autowired
    PersonService personService;

    @GetMapping()
    public String showUserInfo(Model model, Principal principal) {
      Person person = personService.findByUsername(principal.getName());
      model.addAttribute("person", person);

        return "userInfo";
    }

    @GetMapping("user/showUser")
    public String pageUser(Principal principal) {
        return "USER page " + principal.getName();
    }

}
