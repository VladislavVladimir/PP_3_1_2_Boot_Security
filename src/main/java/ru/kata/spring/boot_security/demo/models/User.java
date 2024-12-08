package ru.kata.spring.boot_security.demo.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Size(min = 2, max = 30, message = "Имя должно содержать от 2 до 30 символов")
    private String name;

    @Size(min = 2, max = 30, message = "Фамилия должна содержать от 2 до 30 символов")
    private String lastName;

    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 150, message = "Возраст не может превышать 150 лет")
    private int age;

    @Column(unique = true)
    @Size(min = 4, max = 50, message = "Логин должен содержать от 4 до 50 символов")
    private String username;
    @NotBlank
    private String password;
    @Email(message = "Введите корректный email")
    @Column(unique = true)
    private String email;
    @Pattern(regexp = "\\+?[0-9]{10,15}", message = "Введите корректный номер телефона")
    @Column(unique = true)
    private String phone;

    private Date timeRegister;       //дата регистрации

    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public void addRole(Role role) {
        roles.add(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}