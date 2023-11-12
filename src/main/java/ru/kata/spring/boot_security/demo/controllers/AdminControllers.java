package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.method.P;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Person;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.repository.RoleRepository;
import ru.kata.spring.boot_security.demo.service.PersonService;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.util.PersonValidator;

import javax.validation.Valid;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminControllers {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PersonService personService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private PersonValidator personValidator;

    @GetMapping()
    public String show(Model model, Principal principal) {
        model.addAttribute("person", new Person());


        Person adminPerson = personService.findByUsername(principal.getName());
        model.addAttribute("adminPerson", adminPerson);


        List<String> roleRaw = adminPerson.getRoles().stream()
                .map(Role::getName).collect(Collectors.toList());

        System.out.println(roleRaw);

        List<String> roleNames = new ArrayList<>();


            if (roleRaw.contains("ROLE_ADMIN")) {
                roleNames.add("ADMIN");
            }
            if (roleRaw.contains("ROLE_USER")) {
                roleNames.add("USER");
            }

        model.addAttribute("roleNames", roleNames);


        List<Person> persons = personService.listPerson(); // Получение списка пользователей
        model.addAttribute("persons", persons); // Передача списка пользователей в представление
        return "adminShow";
    }

    @GetMapping("/user")
    public String showAdminInfo(Model model, Principal principal) {

        Person person = personService.findByUsername(principal.getName());
        model.addAttribute("person", person);

        List<String> roleRaw = person.getRoles().stream()
                .map(Role::getName).collect(Collectors.toList());

        System.out.println(roleRaw);

        List<String> roleNames = new ArrayList<>();


        if (roleRaw.contains("ROLE_ADMIN")) {
            roleNames.add("ADMIN");
        }
        if (roleRaw.contains("ROLE_USER")) {
            roleNames.add("USER");
        }

        model.addAttribute("roleNames", roleNames);

        return "adminUser";
    }

    @GetMapping("/new")
    public String newUser(Model model) {
        model.addAttribute("person", new Person());

        return "adminShow";
    }


    @PostMapping("/create")
    public String createPersonWithRoles(@ModelAttribute("person") Person person,
                                        BindingResult bindingResult,
                                        @RequestParam(name = "selectedRoles", required = false) List<String> selectedRoles) {
        personValidator.validate(person, bindingResult);

        if (bindingResult.hasErrors()) {
            return "adminShow";
        }

        Set<Role> roles = new HashSet<>();

        for (String roleName : selectedRoles) {
            if (roleName.contains("ADMIN")) {
                roles.add(roleService.findByName("ROLE_ADMIN"));
            }
            if (roleName.contains("USER")) {
                roles.add(roleService.findByName("ROLE_USER"));
            }

        }
        personService.savePersonWithRoles(person, roles);
        return "redirect:/admin";
    }


    @DeleteMapping("/delete")
    public String deletePerson(@RequestParam(name = "id") int id) {
        personService.deleteById(id);
        return "redirect:/admin";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") int id, Model model) {

        Person person = personService.findById(id);
        System.out.println("!!!!!");
        System.out.println(person.toString());
        System.out.println("!!!!!");
        model.addAttribute("person", personService.findById(id));
        return "adminShow";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person, @PathVariable("id") int id,
                         BindingResult bindingResult,
                         @RequestParam(name = "selectedRoles", required = false) List<String> selectedRoles) {

        personValidator.validate(person, bindingResult);

        Person otherPerson = personService.findByUsername(person.getUsername());
        int otherPersonId = (otherPerson != null) ? otherPerson.getId() : 0;

        if (bindingResult.hasErrors() && id != otherPersonId) {
            return "edit";
        }

        Set<Role> roles = new HashSet<>();
        for (String roleName : selectedRoles) {
            if (roleName.contains("ADMIN")) {
                roles.add(roleService.findByName("ROLE_ADMIN"));
            }
            if (roleName.contains("USER")) {
                roles.add(roleService.findByName("ROLE_USER"));
            }

        }

        personService.updatePersonWithRoles(person, id, roles);
        return "redirect:/admin";
    }
}
