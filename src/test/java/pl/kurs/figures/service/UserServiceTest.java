package pl.kurs.figures.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.kurs.figures.model.Role;
import pl.kurs.figures.model.User;
import pl.kurs.figures.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        userRepository = Mockito.mock(UserRepository.class);
        roleService = Mockito.mock(RoleService.class);
        passwordEncoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService(userRepository, roleService, passwordEncoder);
    }

    @Test
    public void addTestNullFields() {
        User dummy = new User("adam", "kocik", "adam123", "123");
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        given(passwordEncoder.encode(ArgumentMatchers.anyString())).willReturn("asfsdgsfdghdsehgfrdfhg");
        given(roleService.getRole(ArgumentMatchers.anyString())).willReturn(userNewRole);

        User user = userService.add(dummy);

        assertThat(user).isNull();

    }

    @Test
    public void findByLoginTest() {
        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);

        given(userRepository.findByLogin(ArgumentMatchers.anyString())).willReturn(java.util.Optional.of(dummy));

        User adam123 = userService.findByLogin("adam123");

        assertThat(adam123).isNotNull();
    }

    @Test
    public void findByLoginWrongLoginShouldThrowExceptionTest() {
        assertThrows(UsernameNotFoundException.class, () -> userService.findByLogin("huj"));
    }



}