package pl.kurs.figures.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.figures.model.Role;
import pl.kurs.figures.model.User;
import pl.kurs.figures.utils.JwtTokenUtil;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
public class FigureControllerSearchMethodTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private Authentication auth;

    private String token;

    @Autowired
    private JwtTokenUtil jwtUtil;

    private String refreshToken;

    @Autowired
    private AuthenticationManager authManager;


    @BeforeEach
    public void setUp() {
        Mockito.when(auth.getName()).thenReturn("admin");
        SecurityContextHolder.getContext().setAuthentication(auth);
        User dummy = new User("Admin", "Adminowski", "admin", "admin123");
        token = jwtUtil.generateAccessToken(dummy);
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(
                dummy.getLogin(), dummy.getPassword()));
        refreshToken = jwtUtil.generateRefreshToken(authentication);
    }

    @Test
    @Sql(statements = {
            "INSERT INTO figures.abstract_figure values ('Circle', 1, '2023-01-05 20:26:12', '2023-01-05 20:26:12', 'adam123', 0, 50, null, null, null, 1)",
            "INSERT INTO figures.abstract_figure values ('Circle', 2, '2023-01-04 20:26:12', '2023-01-04 20:26:12', 'adam123', 0, 100, null, null, null, 1)",
            "INSERT INTO figures.abstract_figure values ('Square', 3, '2023-01-03 20:26:12', '2023-01-03 20:26:12', 'adam123', 0, null, null, null, 50, 1)"
    })
    @Transactional
    public void searchFigureTestFindFigureByTypeAndArea() throws Exception {

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shapes?type=circle&areaFrom=8000")
                .with(user(dummy))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }


    @Test
    @Sql(statements = {
            "INSERT INTO figures.abstract_figure values ('Circle', 1, '2023-01-05 20:26:12', '2023-01-05 20:26:12', 'adam123', 0, 50, null, null, null, 1)",
            "INSERT INTO figures.abstract_figure values ('Circle', 2, '2023-01-04 20:26:12', '2023-01-04 20:26:12', 'adam123', 0, 100, null, null, null, 1)",
            "INSERT INTO figures.abstract_figure values ('Square', 3, '2023-01-03 20:26:12', '2023-01-03 20:26:12', 'adam123', 0, null, null, null, 50, 1)",
    })
    @Transactional
    public void searchFigureTestFindFigureByDateAndType() throws Exception {

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shapes?type=circle&createdAtFrom=2023-01-04")
                .with(user(dummy))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    @Sql(statements = {
            "INSERT INTO figures.abstract_figure values ('Circle', 1, '2023-01-05 20:26:12', '2023-01-05 20:26:12', 'adam123', 0, 50, null, null, null, 1)",
            "INSERT INTO figures.abstract_figure values ('Circle', 2, '2023-01-04 20:26:12', '2023-01-04 20:26:12', 'adam123', 0, 100, null, null, null, 1)",
            "INSERT INTO figures.abstract_figure values ('Square', 3, '2023-01-03 20:26:12', '2023-01-03 20:26:12', 'adam123', 0, null, null, null, 50, 1)",
    })
    @Transactional
    public void searchFigureTestFindFigureByPerimeter() throws Exception {

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shapes?perimeterFrom=200")
                .with(user(dummy))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }


}
