package pl.kurs.figures.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import pl.kurs.figures.model.Role;
import pl.kurs.figures.repository.RoleRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    public void setup() {
        roleRepository = Mockito.mock(RoleRepository.class);
        roleService = new RoleService(roleRepository);
    }

    @Test
    public void getRoleTest() {
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);

        given(roleRepository.findByName(ArgumentMatchers.anyString())).willReturn(java.util.Optional.of(userNewRole));

        Role role = roleService.getRole("CREATOR");

        assertThat(role).isNotNull();
    }
}