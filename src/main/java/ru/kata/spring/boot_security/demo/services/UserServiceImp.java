package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class UserServiceImp implements UserService, UserDetailsService {
    private final UserDao userDao;
    private final RoleDao roleDao;

    @Autowired
    public UserServiceImp(UserDao userDao, RoleDao roleDao) {
        this.userDao = userDao;
        this.roleDao = roleDao;
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
    public void updateUser(User user, BindingResult bindingResult) {
        if (hasProblemValid(user, bindingResult)) return;

        User updateUser = userDao.findByUsername(user.getUsername());
        if (!updateUser.getPassword().equals(user.getPassword())) { //если пароль был изменен, то его нужно зашифровать
            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        }
        user.setTimeRegister(updateUser.getTimeRegister()); // дата регистрации пользователя не меняется
        userDao.save(user);
    }

    @Override
    public void createUser(User user, BindingResult bindingResult) {
        if (hasProblemValid(user, bindingResult)) return;

        user.setTimeRegister(new Date());   //дата регистрации
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        if (user.getRoles().isEmpty()) {    //настройки для регистрации новых пользователей
            user.addRole(roleDao.findByName("ROLE_USER"));  //роль пользователя по умолчанию
        }

        //функция для создания самого первого админа, доступна в версии SNAPSHOT 1.0
        if (Objects.equals(user.getUsername(), "admin")) {
           Role roleAdmin = roleDao.findByName("ROLE_ADMIN");
           user.addRole(roleAdmin);
        }
        userDao.save(user);
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
        return user;
    }
}
