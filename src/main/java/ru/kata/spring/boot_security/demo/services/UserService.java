package ru.kata.spring.boot_security.demo.services;

import org.springframework.validation.BindingResult;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.List;

public interface UserService {
    void updateUser(User user, BindingResult bindingResult);
    void createUser(User user, BindingResult bindingResult);
    List<User> listUsers();
    User getUser(Long id);
    void deleteUser(Long id);
    User getUserByUsername(String username);
}
