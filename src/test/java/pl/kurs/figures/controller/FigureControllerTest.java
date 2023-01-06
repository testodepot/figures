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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.command.EditFigureCommand;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.AbstractFigureView;
import pl.kurs.figures.model.Circle;
import pl.kurs.figures.model.Square;
import pl.kurs.figures.security.JwtTokenUtil;
import pl.kurs.figures.security.Role;
import pl.kurs.figures.security.User;
import pl.kurs.figures.security.UserService;
import pl.kurs.figures.service.AbstractFigureService;
import pl.kurs.figures.service.AbstractFigureViewService;
import pl.kurs.figures.service.ObjectMaker;
import pl.kurs.figures.strategy.*;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;

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
    private AbstractFigureViewService abstractFigureViewServiceMock;

    @Autowired
    private ObjectMaker objectMaker;

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

        AbstractFigureView abstractFigureView = new AbstractFigureView();
        abstractFigureView.setPerimeter(BigDecimal.valueOf(320));
        abstractFigureView.setArea(BigDecimal.valueOf(6400));

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
        Mockito.when(abstractFigureViewServiceMock.computeAreaAndPerimeterForFigureView(ArgumentMatchers.isA(CreateFigureCommand.class))).thenReturn(abstractFigureView);
        abstractFigureView.setModelId(1L);
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
    public void searchFigureTest() throws Exception {

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);
        token = jwtUtil.generateAccessToken(dummy);

        List<AbstractFigure> figures = new ArrayList<>();

        Circle circle = new Circle(BigDecimal.valueOf(35));
        circle.setId(1L);
        circle.setCreatedAt(Date.valueOf("2022-12-19"));
        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
        circle.setCreatedBy("adam");
        circle.setType("Circle");

        figures.add(circle);

        Page<AbstractFigure> page = new PageImpl<>(figures);

        AbstractFigureView abstractFigureView = new AbstractFigureView();
        abstractFigureView.setPerimeter(BigDecimal.valueOf(219.8));
        abstractFigureView.setArea(BigDecimal.valueOf(3846.5));

        Mockito.when(abstractFigureServiceMock.findAll(ArgumentMatchers.isA(Specification.class), ArgumentMatchers.isA(Pageable.class))).thenReturn(page);
        Mockito.when(abstractFigureViewServiceMock.getById(ArgumentMatchers.isA(Long.class))).thenReturn(abstractFigureView);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shapes?type=circle&areaFrom=500&createdAtFrom=2022-12-18")
                .with(user(dummy))
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }


//    @Test
//    @Sql(statements = {
//            "INSERT INTO figures.view_abstract_figure values (1, 7850.00, 314.00)",
//            "INSERT INTO figures.abstract_figure values ('Circle', 1, '2023-01-05 20:26:12', 'adam123', '2023-01-05 20:26:12', 'adam123', 0, 50, null, null, null, 1)",
//            "INSERT INTO figures.view_abstract_figure values (2, 31400.00, 628.00)",
//            "INSERT INTO figures.abstract_figure values ('Circle', 2, '2023-01-04 20:26:12', 'adam123', '2023-01-04 20:26:12', 'adam123', 0, 100, null, null, null, 2)",
//            "INSERT INTO figures.view_abstract_figure values (3, 2500.00, 200.00)",
//            "INSERT INTO figures.abstract_figure values ('Square', 3, '2023-01-03 20:26:12', 'adam123', '2023-01-03 20:26:12', 'adam123', 0, null, null, null, 50, 3)",
//    })
//    @Transactional
//    public void searchFigureTestFind() throws Exception {
//
//        User dummy = new User("adam", "kocik", "adam123", "123");
//        Set<Role> userRoleSet = new HashSet<>();
//        Role userNewRole = new Role("CREATOR");
//        userNewRole.setId(1);
//        userRoleSet.add(userNewRole);
//        dummy.setRoles(userRoleSet);
//        token = jwtUtil.generateAccessToken(dummy);
//
//        List<AbstractFigure> figures = new ArrayList<>();
//
//        Circle circle = new Circle(BigDecimal.valueOf(35));
//        circle.setId(1L);
//        circle.setCreatedAt(Date.valueOf("2022-12-19"));
//        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
//        circle.setCreatedBy("adam");
//        circle.setType("Circle");
//
//        figures.add(circle);
//
//        Page<AbstractFigure> page = new PageImpl<>(figures);
//
//        AbstractFigureView abstractFigureView = new AbstractFigureView();
//        abstractFigureView.setPerimeter(BigDecimal.valueOf(219.8));
//        abstractFigureView.setArea(BigDecimal.valueOf(3846.5));
//
//        Specification<AbstractFigure> specification = Specification.where(new AbstractFigureWithTypeEqual("Circle"))
//                .and(new AbstractFigureWithAreaFrom(BigDecimal.valueOf(8000)));
//
//        Pageable paging = PageRequest.of(0, 2);
//
//
////        Mockito.when(abstractFigureService.findAll(specification, paging)).thenCallRealMethod();
////        Mockito.when(abstractFigureViewService.getById(ArgumentMatchers.isA(Long.class))).thenCallRealMethod();
//
//        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shapes?type=circle&areaFrom=8000")
//                .with(user(dummy))
//                .header("Authorization", token)
//                .contentType(MediaType.APPLICATION_JSON_VALUE))
//                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
//                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
//    }


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
        circle.setCreatedBy("adam123");
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

        AbstractFigureView abstractFigureView = new AbstractFigureView();
        abstractFigureView.setPerimeter(BigDecimal.valueOf(314));
        abstractFigureView.setArea(BigDecimal.valueOf(7850));

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
        SecurityContextHolder.getContext().getAuthentication().getName();
        Mockito.when(userService.findByLogin(ArgumentMatchers.isA(String.class))).thenReturn(newUser);
        Mockito.doNothing().when(abstractFigureServiceMock).checkIfUserHasPermissionToEdit(ArgumentMatchers.isA(User.class), ArgumentMatchers.isA(AbstractFigure.class));
        Mockito.when(abstractFigureServiceMock.editObject(ArgumentMatchers.isA(EditFigureCommand.class))).thenReturn(editedCircle);
        Mockito.when(abstractFigureViewServiceMock.computeAreaAndPerimeterForEditedFigure(ArgumentMatchers.isA(String.class), ArgumentMatchers.isA(List.class))).thenReturn(abstractFigureView);
        abstractFigureView.setModelId(1L);
        editedCircle.setAbstractFigureView(abstractFigureView);
        Mockito.when(abstractFigureViewServiceMock.edit(ArgumentMatchers.isA(AbstractFigureView.class))).thenReturn(abstractFigureView);
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
        circle.setCreatedBy("adam123");
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
        circle.setCreatedBy("adam123");
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
        circle.setCreatedBy("adam123");
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
        circle.setCreatedBy("adam123");
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
        circle.setCreatedBy("adam123");
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
        circle.setCreatedBy("adam123");
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