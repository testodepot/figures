package pl.kurs.figures.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.figures.command.CreateFigureCommand;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Circle;
import pl.kurs.figures.repository.AbstractFigureBaseRepository;
import pl.kurs.figures.service.AbstractFigureService;
import pl.kurs.figures.service.ObjectMaker;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;



@SpringBootTest
@AutoConfigureMockMvc
public class FigureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private AbstractFigureService abstractFigureService;

    @Mock
    private ObjectMaker objectMaker;

    @Mock
    private AbstractFigureBaseRepository abstractFigureBaseRepository;


    @Test
    @WithMockUser(username = "adam", password = "123", roles = {"ADMIN", "CREATOR"})
    public void addFigureTest() throws Exception {

        Circle circle = new Circle(BigDecimal.valueOf(25));

        Mockito.when(objectMaker.makeObject(ArgumentMatchers.isA(CreateFigureCommand.class))).thenCallRealMethod();
        Mockito.when(abstractFigureService.createSpecificFigure(ArgumentMatchers.isA(CreateFigureCommand.class))).thenCallRealMethod();
        Mockito.when(abstractFigureService.add(ArgumentMatchers.isA(AbstractFigure.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
                .content("{\n" +
                        "  \"type\": \"circle\",\n" +
                        "  \"parameters\": [\n" +
                        "    25\n" +
                        "  ]\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }


    @Test
    @WithMockUser(username = "adam", password = "123", roles = {"ADMIN", "CREATOR"})
    public void addFigureTestWrongTypeOfFigureShouldFail() throws Exception {

        Circle circle = new Circle(BigDecimal.valueOf(25));

        Mockito.when(abstractFigureService.createSpecificFigure(ArgumentMatchers.isA(CreateFigureCommand.class))).thenCallRealMethod();

        Mockito.when(abstractFigureService.add(ArgumentMatchers.isA(AbstractFigure.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
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
    @WithMockUser(username = "adam", password = "123", roles = {"ADMIN", "CREATOR"})
    public void addFigureTestWrongTypeOfFigureNumberShouldFail() throws Exception {

        Circle circle = new Circle(BigDecimal.valueOf(25));

        Mockito.when(abstractFigureService.createSpecificFigure(ArgumentMatchers.isA(CreateFigureCommand.class))).thenCallRealMethod();

        Mockito.when(abstractFigureService.add(ArgumentMatchers.isA(AbstractFigure.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
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
    @WithMockUser(username = "adam", password = "123", roles = {"ADMIN", "CREATOR"})
    public void addFigureTestWrongTypeOfParamsForSpecificFigureShouldFail() throws Exception {

        Circle circle = new Circle(BigDecimal.valueOf(25));

        Mockito.when(abstractFigureService.createSpecificFigure(ArgumentMatchers.isA(CreateFigureCommand.class))).thenCallRealMethod();

        Mockito.when(abstractFigureService.add(ArgumentMatchers.isA(AbstractFigure.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
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
    @WithMockUser(username = "adam", password = "123", roles = {"ADMIN", "CREATOR"})
    public void addFigureTestWrongTypeOfParamShouldFail() throws Exception {

        Circle circle = new Circle(BigDecimal.valueOf(25));

        Mockito.when(abstractFigureService.createSpecificFigure(ArgumentMatchers.isA(CreateFigureCommand.class))).thenCallRealMethod();

        Mockito.when(abstractFigureService.add(ArgumentMatchers.isA(AbstractFigure.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
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
    @WithMockUser(username = "adam", password = "123", roles = {"ADMIN", "CREATOR"})
    public void addFigureTestEmptyListOfParamsShouldFail() throws Exception {

        Circle circle = new Circle(BigDecimal.valueOf(25));

        Mockito.when(abstractFigureService.createSpecificFigure(ArgumentMatchers.isA(CreateFigureCommand.class))).thenCallRealMethod();

        Mockito.when(abstractFigureService.add(ArgumentMatchers.isA(AbstractFigure.class))).thenReturn(circle);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/shapes")
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
    @WithMockUser(username = "adam", password = "123", roles = {"ADMIN", "CREATOR"})
    public void searchFigureTest() throws Exception {

        List<AbstractFigure> figures = new ArrayList<>();

        Circle circle = new Circle(BigDecimal.valueOf(35));
        circle.setId(1L);
        circle.setCreatedAt(Date.valueOf("2022-12-19"));
        circle.setLastModifiedAt(Date.valueOf("2022-12-19"));
        circle.setCreatedBy("adam");
        circle.setType("Circle");

        figures.add(circle);

        Page<AbstractFigure> page = new PageImpl<>(figures);

        Mockito.when(abstractFigureService.findAll(ArgumentMatchers.isA(Specification.class), ArgumentMatchers.isA(Pageable.class))).thenReturn(page);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/shapes?type=circle&areaFrom=500&createdAtFrom=2022-12-18")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }




}