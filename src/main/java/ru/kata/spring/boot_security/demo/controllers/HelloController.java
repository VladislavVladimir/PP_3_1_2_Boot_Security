package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.UserService;

import javax.validation.Valid;
import java.security.Principal;
import java.util.stream.Collectors;

@Controller
public class HelloController {
	private final UserService userService;

	@Autowired
	public HelloController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping(value = "/")
	public String printWelcome(ModelMap model, Principal principal) {
		if (principal != null) {
			model.addAttribute("username", principal.getName());
			model.addAttribute("roles",
					((Authentication)principal).getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
		}
		return "index";
	}

	@GetMapping("/registration")
	public String showRegistrationForm(Model model) {
		model.addAttribute("user", new User());
		return "registration";
	}

	@PostMapping("/registration")
	public String registerUser (@Valid @ModelAttribute("user") User user, BindingResult bindingResult) {
		userService.createUser(user, bindingResult);
		if (bindingResult.hasErrors()) {
			return "registration";
		}
		return "redirect:/user";
	}
}