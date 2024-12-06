package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.models.Role;

import java.util.List;

@Service
@Transactional
public class RoleServiceImp implements RoleService {
    private final RoleDao roleDao;

    @Autowired
    public RoleServiceImp(RoleDao roleDao) {
        this.roleDao = roleDao;

        //стартовая настройка ролей, доступна в версии SNAPSHOT 1.0
        createIfNotExists(new Role("ROLE_ADMIN"));      //роль админа
        createIfNotExists(new Role("ROLE_USER"));       //роль обычного пользователя
        createIfNotExists(new Role("ROLE_MODERATOR"));  //роли без бизнес-логики - для демонстрации
        createIfNotExists(new Role("ROLE_SUPERADMIN"));
        createIfNotExists(new Role("ROLE_MANAGER"));
    }

    @Override
    public void createIfNotExists(Role role) {
        Role existingRole = roleDao.findByName(role.getName());
        if (existingRole == null) {
            roleDao.save(role); // Если роль не существует, создаем новую
        }
    }

    @Override
    public List<Role> listRoles() {
        return roleDao.findAll();
    }
}
