package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminsController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminsController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public String printUsers(Model model) {
        model.addAttribute("users", userService.listUsers());
        return "/admin/users";
    }

    @GetMapping("/create")
    public String createUser(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("availableRoles", roleService.listRoles());
        model.addAttribute("infoText", "Заполните данные нового пользователя:");
        return "/admin/edit";
    }

    @GetMapping("/edit")
    public String updateUser(@RequestParam(name = "id", required = false, defaultValue = "0") Long id, Model model) {
        User user = userService.getUser(id);
        if (user == null) return "redirect:/users/create";
        model.addAttribute("user", user);
        model.addAttribute("availableRoles", roleService.listRoles());
        model.addAttribute("infoText", String.format("Измените данные пользователя с id=%d:", id));
        return "admin/edit";
    }

    @GetMapping("/delete")
    public String deleteUser(@RequestParam(name = "id", required = false, defaultValue = "0") Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @PostMapping("/edit")
    public String updateUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        userService.saveUser(user, bindingResult);
        if (bindingResult.hasErrors()) {
            model.addAttribute("infoText", "Пожалуйста, исправьте ошибки в форме.");
            model.addAttribute("availableRoles", roleService.listRoles());
            return "admin/edit";
        }
        return "redirect:/admin/users";
    }
}