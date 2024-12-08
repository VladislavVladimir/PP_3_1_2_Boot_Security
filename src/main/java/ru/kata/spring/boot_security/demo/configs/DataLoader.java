package ru.kata.spring.boot_security.demo.configs;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.services.RoleService;
import ru.kata.spring.boot_security.demo.services.UserService;

import java.util.Set;

@Component
public class DataLoader implements CommandLineRunner {
    private final RoleService roleService;
    private final UserService userService;

    public DataLoader(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(String... args) {
        //заполняем таблицу начальными значениями
        Role roleAdmin = roleService.saveIfNotExists(new Role("ROLE_ADMIN"));
        Role roleUser = roleService.saveIfNotExists(new Role("ROLE_USER"));

        userService.saveUser(new User(0, "Вася", "Пупкин", 9, "admin", "admin",
                "admin@mail.ru","+79997779977",null, Set.of(roleAdmin, roleUser)));
        userService.saveUser(new User(0, "John", "Doe", 83, "user", "user",
                "user@mail.ru","+79088423571",null, Set.of(roleUser)));
    }
}
