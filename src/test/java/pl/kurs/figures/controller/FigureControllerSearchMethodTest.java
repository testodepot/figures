package pl.kurs.figures.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.figures.repository.AbstractFigureBaseRepository;
import pl.kurs.figures.repository.AbstractFigureViewRepository;
import pl.kurs.figures.security.JwtTokenUtil;
import pl.kurs.figures.security.Role;
import pl.kurs.figures.security.User;
import pl.kurs.figures.service.AbstractFigureService;
import pl.kurs.figures.service.AbstractFigureViewService;
import pl.kurs.figures.service.ObjectMaker;
import pl.kurs.figures.strategy.ComputingStrategyFactory;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
public class FigureControllerSearchMethodTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AbstractFigureService abstractFigureService;

    @Autowired
    private AbstractFigureViewService abstractFigureViewService;

    @Autowired
    private AbstractFigureBaseRepository abstractFigureBaseRepository;

    @Autowired
    private AbstractFigureViewRepository abstractFigureViewRepository;

    @Autowired
    private ObjectMaker objectMaker;

    @Autowired
    private ComputingStrategyFactory computingStrategyFactory;

    @Mock
    private Authentication auth;

    private String token;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        Mockito.when(auth.getName()).thenReturn("adam123");
        SecurityContextHolder.getContext().setAuthentication(auth);
        abstractFigureService = new AbstractFigureService(abstractFigureBaseRepository, objectMaker);
        abstractFigureViewService = new AbstractFigureViewService(computingStrategyFactory, abstractFigureViewRepository);
    }

    @Test
    @Sql(statements = {
            "INSERT INTO figures.view_abstract_figure values (1, 7850.00, 314.00)",
            "INSERT INTO figures.abstract_figure values ('Circle', 1, '2023-01-05 20:26:12', 'adam123', '2023-01-05 20:26:12', 'adam123', 0, 50, null, null, null, 1)",
            "INSERT INTO figures.view_abstract_figure values (2, 31400.00, 628.00)",
            "INSERT INTO figures.abstract_figure values ('Circle', 2, '2023-01-04 20:26:12', 'adam123', '2023-01-04 20:26:12', 'adam123', 0, 100, null, null, null, 2)",
            "INSERT INTO figures.view_abstract_figure values (3, 2500.00, 200.00)",
            "INSERT INTO figures.abstract_figure values ('Square', 3, '2023-01-03 20:26:12', 'adam123', '2023-01-03 20:26:12', 'adam123', 0, null, null, null, 50, 3)",
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
            "INSERT INTO figures.view_abstract_figure values (1, 7850.00, 314.00)",
            "INSERT INTO figures.abstract_figure values ('Circle', 1, '2023-01-05 20:26:12', 'adam123', '2023-01-05 20:26:12', 'adam123', 0, 50, null, null, null, 1)",
            "INSERT INTO figures.view_abstract_figure values (2, 31400.00, 628.00)",
            "INSERT INTO figures.abstract_figure values ('Circle', 2, '2023-01-04 20:26:12', 'adam123', '2023-01-04 20:26:12', 'adam123', 0, 100, null, null, null, 2)",
            "INSERT INTO figures.view_abstract_figure values (3, 2500.00, 200.00)",
            "INSERT INTO figures.abstract_figure values ('Square', 3, '2023-01-03 20:26:12', 'adam123', '2023-01-03 20:26:12', 'adam123', 0, null, null, null, 50, 3)",
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
            "INSERT INTO figures.view_abstract_figure values (1, 7850.00, 314.00)",
            "INSERT INTO figures.abstract_figure values ('Circle', 1, '2023-01-05 20:26:12', 'adam123', '2023-01-05 20:26:12', 'adam123', 0, 50, null, null, null, 1)",
            "INSERT INTO figures.view_abstract_figure values (2, 31400.00, 628.00)",
            "INSERT INTO figures.abstract_figure values ('Circle', 2, '2023-01-04 20:26:12', 'adam123', '2023-01-04 20:26:12', 'adam123', 0, 100, null, null, null, 2)",
            "INSERT INTO figures.view_abstract_figure values (3, 2500.00, 200.00)",
            "INSERT INTO figures.abstract_figure values ('Square', 3, '2023-01-03 20:26:12', 'adam123', '2023-01-03 20:26:12', 'adam123', 0, null, null, null, 50, 3)",
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

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shapes?perimeterFrom=250")
                .with(user(dummy))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }


}
