package pl.kurs.figures.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.model.*;
import pl.kurs.figures.service.AbstractFigureService;
import pl.kurs.figures.service.UserService;
import pl.kurs.figures.strategy.*;
import pl.kurs.figures.utils.JwtTokenUtil;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


@SpringBootTest
@AutoConfigureMockMvc
public class FigureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AbstractFigureService abstractFigureServiceMock;

    @MockBean
    private UserService userService;

    private String token;

    @Autowired
    private JwtTokenUtil jwtUtil;

    @MockBean
    private CreatingStrategyFactory creatingStrategyFactory;

    @Mock
    private Authentication auth;


    @BeforeEach
    public void setUp() {
        Mockito.when(auth.getName()).thenReturn("adam123");
        SecurityContextHolder.getContext().setAuthentication(auth);
    }


    @Test
    public void addFigureTest() throws Exception {

        User newUser = new User("adam", "kocik", "adam123", "123");
        newUser.setId(1);
        Set<AbstractFigure> figuresSet = new HashSet<>();
        Square figure = new Square(BigDecimal.valueOf(80));
        figure.setId(1L);
        figure.setType("Square");
        figuresSet.add(figure);
        Set<Role> roleSet = new HashSet<>();
        Role newRole = new Role("CREATOR");
        newRole.setId(1);
        roleSet.add(newRole);
        newUser.setRoles(roleSet);


        CreatingStrategy creatingStrategyCircle = new CreateCircle();
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        CreatingStrategy creatingStrategySquare = new CreateSquare();

        Map<String, CreatingStrategy> strategies = new HashMap<>();
        strategies.put(creatingStrategyCircle.getStrategyName(), creatingStrategyCircle);
        strategies.put(creatingStrategyRectangle.getStrategyName(), creatingStrategyRectangle);
        strategies.put(creatingStrategySquare.getStrategyName(), creatingStrategySquare);

        Mockito.when(creatingStrategyFactory.getStrategies()).thenReturn(strategies);
        Mockito.when(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).thenReturn(creatingStrategyCircle);

        Mockito.when(abstractFigureServiceMock.createSpecificFigure(ArgumentMatchers.isA(CreateFigureCommand.class))).thenReturn(figure);
        SecurityContextHolder.getContext().getAuthentication().getName();
        Mockito.when(userService.findByLogin(ArgumentMatchers.isA(String.class))).thenReturn(newUser);
        Mockito.when(abstractFigureServiceMock.add(ArgumentMatchers.isA(AbstractFigure.class))).thenReturn(figure);
        newUser.setCreatedFigures(figuresSet);
        Mockito.when(userService.edit(ArgumentMatchers.isA(User.class))).thenReturn(newUser);

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"type\": \"square\",\n" +
                        "  \"parameters\": [\n" +
                        "    80\n" +
                        "  ]\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }


    @Test
    public void addFigureTestWrongTypeOfFigureShouldFail() throws Exception {
        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"type\": \"triangle\",\n" +
                        "  \"parameters\": [\n" +
                        "    25\n" +
                        "  ]\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addFigureTestWrongTypeOfFigureNumberShouldFail() throws Exception {
        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"type\": \"989\",\n" +
                        "  \"parameters\": [\n" +
                        "    25\n" +
                        "  ]\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addFigureTestWrongTypeOfParamsForSpecificFigureShouldFail() throws Exception {
        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"type\": \"circle\",\n" +
                        "  \"parameters\": [\n" +
                        "    25, 34\n" +
                        "  ]\n" +
                        "}\n" +
                        "  ")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addFigureTestWrongTypeOfParamShouldFail() throws Exception {
        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"type\": \"circle\",\n" +
                        "  \"parameters\": [\n" +
                        "    dup\n" +
                        "  ]\n" +
                        "}\n" +
                        "  ")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void addFigureTestEmptyListOfParamsShouldFail() throws Exception {
        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"type\": \"circle\",\n" +
                        "  \"parameters\": [\n" +
                        "  ]\n" +
                        "}\n" +
                        "  ")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }



    @Test
    public void editFigureTest() throws Exception {

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        Circle circle = new Circle(BigDecimal.valueOf(35));
        circle.setId(1L);
        circle.setCreatedAt(Date.valueOf("2022-12-19"));
        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
        circle.setCreatedBy(dummy);
        circle.setType("Circle");

        User newUser = new User("adam", "kocik", "adam123", "123");
        newUser.setId(1);
        Set<AbstractFigure> figuresSet = new HashSet<>();
        Square figure = new Square(BigDecimal.valueOf(80));
        figure.setId(1L);
        figure.setType("Square");
        figuresSet.add(figure);
        Set<Role> roleSet = new HashSet<>();
        Role newRole = new Role("CREATOR");
        newRole.setId(1);
        roleSet.add(newRole);
        newUser.setRoles(roleSet);

        Circle editedCircle = circle;
        editedCircle.setRadius(BigDecimal.valueOf(50));

        CreatingStrategy creatingStrategyCircle = new CreateCircle();
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        CreatingStrategy creatingStrategySquare = new CreateSquare();

        Map<String, CreatingStrategy> strategies = new HashMap<>();
        strategies.put(creatingStrategyCircle.getStrategyName(), creatingStrategyCircle);
        strategies.put(creatingStrategyRectangle.getStrategyName(), creatingStrategyRectangle);
        strategies.put(creatingStrategySquare.getStrategyName(), creatingStrategySquare);


        Mockito.when(abstractFigureServiceMock.existsById(ArgumentMatchers.isA(Long.class))).thenReturn(true);
        Mockito.when(creatingStrategyFactory.getStrategies()).thenReturn(strategies);
        Mockito.when(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).thenReturn(creatingStrategyCircle);
        Mockito.when(abstractFigureServiceMock.getById(ArgumentMatchers.isA(Long.class))).thenReturn(circle);
        Mockito.when(abstractFigureServiceMock.editObject(ArgumentMatchers.isA(EditFigureCommand.class))).thenReturn(editedCircle);
        Mockito.when(abstractFigureServiceMock.edit(ArgumentMatchers.isA(AbstractFigure.class))).thenReturn(editedCircle);


        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"idFigure\": 1,\n" +
                        "  \"parameters\": [\n" +
                        "    50\n" +
                        "  ]\n" +
                        "}\n" +
                        "  ")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }


    @Test
    public void editFigureTestLoggedUserWhichDidntCreateFigureShouldThrowException() throws Exception {

        User dummy = new User("adam", "kocik", "albi123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);


        Circle circle = new Circle(BigDecimal.valueOf(35));
        circle.setId(1L);
        circle.setCreatedAt(Date.valueOf("2022-12-19"));
        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
        circle.setCreatedBy(dummy);
        circle.setType("Circle");

        User newUser = new User("adam", "kocik", "adam123", "123");
        newUser.setId(1);
        Set<AbstractFigure> figuresSet = new HashSet<>();
        Square figure = new Square(BigDecimal.valueOf(80));
        figure.setId(1L);
        figure.setType("Square");
        figuresSet.add(figure);
        Set<Role> roleSet = new HashSet<>();
        Role newRole = new Role("CREATOR");
        newRole.setId(1);
        roleSet.add(newRole);
        newUser.setRoles(roleSet);

        Circle editedCircle = circle;
        editedCircle.setRadius(BigDecimal.valueOf(50));
        editedCircle.setCreatedBy(newUser);

        CreatingStrategy creatingStrategyCircle = new CreateCircle();
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        CreatingStrategy creatingStrategySquare = new CreateSquare();

        Map<String, CreatingStrategy> strategies = new HashMap<>();
        strategies.put(creatingStrategyCircle.getStrategyName(), creatingStrategyCircle);
        strategies.put(creatingStrategyRectangle.getStrategyName(), creatingStrategyRectangle);
        strategies.put(creatingStrategySquare.getStrategyName(), creatingStrategySquare);


        Mockito.when(abstractFigureServiceMock.existsById(ArgumentMatchers.isA(Long.class))).thenReturn(true);
        Mockito.when(creatingStrategyFactory.getStrategies()).thenReturn(strategies);
        Mockito.when(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).thenReturn(creatingStrategyCircle);
        Mockito.when(abstractFigureServiceMock.getById(ArgumentMatchers.isA(Long.class))).thenReturn(circle);
        Mockito.when(abstractFigureServiceMock.editObject(ArgumentMatchers.isA(EditFigureCommand.class))).thenReturn(editedCircle);
        Mockito.when(abstractFigureServiceMock.edit(ArgumentMatchers.isA(AbstractFigure.class))).thenReturn(editedCircle);


        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"idFigure\": 1,\n" +
                        "  \"parameters\": [\n" +
                        "    50\n" +
                        "  ]\n" +
                        "}\n" +
                        "  ")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }


    @Test
    public void editFigureTestLoggedAdmin() throws Exception {

        User dummy = new User("albert", "kocik", "albi123", "admin123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("ADMIN");
        userNewRole.setId(2);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);


        User newUser = new User("adam", "kocik", "adam123", "123");
        newUser.setId(1);
        Set<AbstractFigure> figuresSet = new HashSet<>();
        Square figure = new Square(BigDecimal.valueOf(80));
        figure.setId(1L);
        figure.setType("Square");
        figuresSet.add(figure);
        Set<Role> roleSet = new HashSet<>();
        Role newRole = new Role("CREATOR");
        newRole.setId(1);
        roleSet.add(newRole);
        newUser.setRoles(roleSet);

        Circle circle = new Circle(BigDecimal.valueOf(35));
        circle.setId(1L);
        circle.setCreatedAt(Date.valueOf("2022-12-19"));
        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
        circle.setCreatedBy(newUser);
        circle.setType("Circle");
        Circle editedCircle = circle;
        editedCircle.setRadius(BigDecimal.valueOf(50));
        editedCircle.setCreatedBy(newUser);

        CreatingStrategy creatingStrategyCircle = new CreateCircle();
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        CreatingStrategy creatingStrategySquare = new CreateSquare();

        Map<String, CreatingStrategy> strategies = new HashMap<>();
        strategies.put(creatingStrategyCircle.getStrategyName(), creatingStrategyCircle);
        strategies.put(creatingStrategyRectangle.getStrategyName(), creatingStrategyRectangle);
        strategies.put(creatingStrategySquare.getStrategyName(), creatingStrategySquare);


        Mockito.when(abstractFigureServiceMock.existsById(ArgumentMatchers.isA(Long.class))).thenReturn(true);
        Mockito.when(creatingStrategyFactory.getStrategies()).thenReturn(strategies);
        Mockito.when(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).thenReturn(creatingStrategyCircle);
        Mockito.when(abstractFigureServiceMock.getById(ArgumentMatchers.isA(Long.class))).thenReturn(circle);
        Mockito.when(abstractFigureServiceMock.editObject(ArgumentMatchers.isA(EditFigureCommand.class))).thenReturn(editedCircle);
        Mockito.when(abstractFigureServiceMock.edit(ArgumentMatchers.isA(AbstractFigure.class))).thenReturn(editedCircle);


        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"idFigure\": 1,\n" +
                        "  \"parameters\": [\n" +
                        "    50\n" +
                        "  ]\n" +
                        "}\n" +
                        "  ")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }


    @Test
    public void editFigureWrongIdShouldFail() throws Exception {

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        Circle circle = new Circle(BigDecimal.valueOf(35));
        circle.setId(1L);
        circle.setCreatedAt(Date.valueOf("2022-12-19"));
        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
        circle.setCreatedBy(dummy);
        circle.setType("Circle");

        CreatingStrategy creatingStrategyCircle = new CreateCircle();
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        CreatingStrategy creatingStrategySquare = new CreateSquare();

        Map<String, CreatingStrategy> strategies = new HashMap<>();
        strategies.put(creatingStrategyCircle.getStrategyName(), creatingStrategyCircle);
        strategies.put(creatingStrategyRectangle.getStrategyName(), creatingStrategyRectangle);
        strategies.put(creatingStrategySquare.getStrategyName(), creatingStrategySquare);

        Mockito.when(abstractFigureServiceMock.existsById(ArgumentMatchers.isA(Long.class))).thenReturn(false);
        Mockito.when(creatingStrategyFactory.getStrategies()).thenReturn(strategies);
        Mockito.when(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).thenReturn(creatingStrategyCircle);
        Mockito.when(abstractFigureServiceMock.getById(ArgumentMatchers.isA(Long.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"idFigure\": 1,\n" +
                        "  \"parameters\": [\n" +
                        "    50\n" +
                        "  ]\n" +
                        "}\n" +
                        "  ")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void editFigureNegativeIdShouldFail() throws Exception {

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        Circle circle = new Circle(BigDecimal.valueOf(35));
        circle.setId(1L);
        circle.setCreatedAt(Date.valueOf("2022-12-19"));
        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
        circle.setCreatedBy(dummy);
        circle.setType("Circle");

        CreatingStrategy creatingStrategyCircle = new CreateCircle();
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        CreatingStrategy creatingStrategySquare = new CreateSquare();

        Map<String, CreatingStrategy> strategies = new HashMap<>();
        strategies.put(creatingStrategyCircle.getStrategyName(), creatingStrategyCircle);
        strategies.put(creatingStrategyRectangle.getStrategyName(), creatingStrategyRectangle);
        strategies.put(creatingStrategySquare.getStrategyName(), creatingStrategySquare);

        Mockito.when(abstractFigureServiceMock.existsById(ArgumentMatchers.isA(Long.class))).thenReturn(false);
        Mockito.when(creatingStrategyFactory.getStrategies()).thenReturn(strategies);
        Mockito.when(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).thenReturn(creatingStrategyCircle);
        Mockito.when(abstractFigureServiceMock.getById(ArgumentMatchers.isA(Long.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"idFigure\": -1,\n" +
                        "  \"parameters\": [\n" +
                        "    50\n" +
                        "  ]\n" +
                        "}\n" +
                        "  ")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void editFigureNullIdShouldFail() throws Exception {

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        Circle circle = new Circle(BigDecimal.valueOf(35));
        circle.setId(1L);
        circle.setCreatedAt(Date.valueOf("2022-12-19"));
        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
        circle.setCreatedBy(dummy);
        circle.setType("Circle");

        CreatingStrategy creatingStrategyCircle = new CreateCircle();
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        CreatingStrategy creatingStrategySquare = new CreateSquare();

        Map<String, CreatingStrategy> strategies = new HashMap<>();
        strategies.put(creatingStrategyCircle.getStrategyName(), creatingStrategyCircle);
        strategies.put(creatingStrategyRectangle.getStrategyName(), creatingStrategyRectangle);
        strategies.put(creatingStrategySquare.getStrategyName(), creatingStrategySquare);

        Mockito.when(abstractFigureServiceMock.existsById(ArgumentMatchers.isA(Long.class))).thenReturn(false);
        Mockito.when(creatingStrategyFactory.getStrategies()).thenReturn(strategies);
        Mockito.when(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).thenReturn(creatingStrategyCircle);
        Mockito.when(abstractFigureServiceMock.getById(ArgumentMatchers.isA(Long.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"idFigure\": null,\n" +
                        "  \"parameters\": [\n" +
                        "    50\n" +
                        "  ]\n" +
                        "}\n" +
                        "  ")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void editFigureStringIdShouldFail() throws Exception {

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        Circle circle = new Circle(BigDecimal.valueOf(35));
        circle.setId(1L);
        circle.setCreatedAt(Date.valueOf("2022-12-19"));
        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
        circle.setCreatedBy(dummy);
        circle.setType("Circle");

        CreatingStrategy creatingStrategyCircle = new CreateCircle();
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        CreatingStrategy creatingStrategySquare = new CreateSquare();

        Map<String, CreatingStrategy> strategies = new HashMap<>();
        strategies.put(creatingStrategyCircle.getStrategyName(), creatingStrategyCircle);
        strategies.put(creatingStrategyRectangle.getStrategyName(), creatingStrategyRectangle);
        strategies.put(creatingStrategySquare.getStrategyName(), creatingStrategySquare);

        Mockito.when(abstractFigureServiceMock.existsById(ArgumentMatchers.isA(Long.class))).thenReturn(false);
        Mockito.when(creatingStrategyFactory.getStrategies()).thenReturn(strategies);
        Mockito.when(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).thenReturn(creatingStrategyCircle);
        Mockito.when(abstractFigureServiceMock.getById(ArgumentMatchers.isA(Long.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"idFigure\": asd,\n" +
                        "  \"parameters\": [\n" +
                        "    50\n" +
                        "  ]\n" +
                        "}\n" +
                        "  ")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }


    @Test
    public void editFigureWrongNumberOfParamsShouldFail() throws Exception {

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        Circle circle = new Circle(BigDecimal.valueOf(35));
        circle.setId(1L);
        circle.setCreatedAt(Date.valueOf("2022-12-19"));
        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
        circle.setCreatedBy(dummy);
        circle.setType("Circle");

        CreatingStrategy creatingStrategyCircle = new CreateCircle();
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        CreatingStrategy creatingStrategySquare = new CreateSquare();

        Map<String, CreatingStrategy> strategies = new HashMap<>();
        strategies.put(creatingStrategyCircle.getStrategyName(), creatingStrategyCircle);
        strategies.put(creatingStrategyRectangle.getStrategyName(), creatingStrategyRectangle);
        strategies.put(creatingStrategySquare.getStrategyName(), creatingStrategySquare);

        Mockito.when(abstractFigureServiceMock.existsById(ArgumentMatchers.isA(Long.class))).thenReturn(true);
        Mockito.when(creatingStrategyFactory.getStrategies()).thenReturn(strategies);
        Mockito.when(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).thenReturn(creatingStrategyCircle);
        Mockito.when(abstractFigureServiceMock.getById(ArgumentMatchers.isA(Long.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"idFigure\": 1,\n" +
                        "  \"parameters\": [\n" +
                        "    80, 20\n" +
                        "  ]\n" +
                        "}\n" +
                        "  ")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void editFigureEmptyListOfParamsShouldFail() throws Exception {

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        Circle circle = new Circle(BigDecimal.valueOf(35));
        circle.setId(1L);
        circle.setCreatedAt(Date.valueOf("2022-12-19"));
        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
        circle.setCreatedBy(dummy);
        circle.setType("Circle");

        CreatingStrategy creatingStrategyCircle = new CreateCircle();
        CreatingStrategy creatingStrategyRectangle = new CreateRectangle();
        CreatingStrategy creatingStrategySquare = new CreateSquare();

        Map<String, CreatingStrategy> strategies = new HashMap<>();
        strategies.put(creatingStrategyCircle.getStrategyName(), creatingStrategyCircle);
        strategies.put(creatingStrategyRectangle.getStrategyName(), creatingStrategyRectangle);
        strategies.put(creatingStrategySquare.getStrategyName(), creatingStrategySquare);

        Mockito.when(abstractFigureServiceMock.existsById(ArgumentMatchers.isA(Long.class))).thenReturn(true);
        Mockito.when(creatingStrategyFactory.getStrategies()).thenReturn(strategies);
        Mockito.when(creatingStrategyFactory.findStrategy(ArgumentMatchers.isA(String.class))).thenReturn(creatingStrategyCircle);
        Mockito.when(abstractFigureServiceMock.getById(ArgumentMatchers.isA(Long.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/shapes")
                .with(user(dummy))
                .header("Authorization", token)
                .content("{\n" +
                        "  \"idFigure\": 1,\n" +
                        "  \"parameters\": [\n" +
                        "\n" +
                        "  ]\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}