package ru.kata.spring.boot_security.demo.services;

import org.hibernate.Hibernate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class UserServiceImp implements UserService, UserDetailsService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImp(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<User> listUsers() {
        return userDao.findAll();
    }

    @Override
    public User getUser(Long id) {
        return userDao.getById(id);
    }

    @Override
    public void deleteUser(Long id) {
        userDao.deleteById(id);
    }

    @Override
    public User getUserByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public void saveUser(User user) {
        if (user.getId() > 0) {
            User updateUser = userDao.getById(user.getId());
            if (!updateUser.getPassword().equals(user.getPassword())) { //если пароль был изменен, то его нужно зашифровать
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            user.setTimeRegister(updateUser.getTimeRegister()); // дата регистрации пользователя не меняется
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setTimeRegister(new Date());   //дата регистрации
        }

        userDao.save(user);
    }

    @Override
    public void saveUser(User user, BindingResult bindingResult) {
        if (hasProblemValid(user, bindingResult)) return;

        saveUser(user);
    }

    private boolean hasProblemValid(User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return true;
        }
        checkUniqueField(user, bindingResult, userDao.findByUsername(user.getUsername()), "username", "Логин уже занят");
        checkUniqueField(user, bindingResult, userDao.findByEmail(user.getEmail()), "email", "Email уже занят");
        checkUniqueField(user, bindingResult, userDao.findByPhone(user.getPhone()), "phone", "Номер телефона уже занят");

        return bindingResult.hasErrors();
    }

    private void checkUniqueField(User user, BindingResult bindingResult, User existing, String fieldName, String errorMsg) {
        if (existing != null && existing.getId() != user.getId()) {
            bindingResult.rejectValue(fieldName, "error.user", errorMsg);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }
        Hibernate.initialize(user.getRoles()); //загружаем роли
        return user;
    }
}
