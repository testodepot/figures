package pl.kurs.figures.controller;


import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.figures.dto.RoleDto;
import pl.kurs.figures.dto.UserDto;
import pl.kurs.figures.model.AbstractFigure;
import pl.kurs.figures.model.Role;
import pl.kurs.figures.model.Square;
import pl.kurs.figures.model.User;
import pl.kurs.figures.service.RoleService;
import pl.kurs.figures.service.UserService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@AutoConfigureMockMvc
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private UserService userService;

    @MockBean
    private RoleService roleService;




    @Test
    public void shouldCreateUser() throws  Exception {

        User newUser = new User("adam", "kocik", "adam123", "123");
        newUser.setId(1);
        Set<AbstractFigure> figuresSet = new HashSet<>();
        Square figure = new Square(BigDecimal.valueOf(80));
        figure.setId(1L);
        figure.setType("Square");
        figuresSet.add(figure);
        newUser.setCreatedFigures(figuresSet);
        Set<Role> roleSet = new HashSet<>();
        Role newRole = new Role("CREATOR");
        newRole.setId(1);
        roleSet.add(newRole);
        newUser.setRoles(roleSet);


        Mockito.when(roleService.getRole(ArgumentMatchers.isA(String.class))).thenReturn(newRole);
        Mockito.when(userService.add(ArgumentMatchers.isA(User.class))).thenReturn(newUser);

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .content("{\n" +
                        "  \"login\": \"adam123\",\n" +
                        "  \"name\": \"Adam\",\n" +
                        "  \"surname\": \"Kocik\",\n" +
                        "  \"password\": \"123\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

    @Test
    public void createUserTestNameWithNumbersShouldFail() throws  Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .content("{\n" +
                        "  \"login\": \"adam123\",\n" +
                        "  \"name\": \"Adam22\",\n" +
                        "  \"surname\": \"Kocik\",\n" +
                        "  \"password\": \"123\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createUserTestTooShortPasswordShouldFail() throws  Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .content("{\n" +
                        "  \"login\": \"adam123\",\n" +
                        "  \"name\": \"Adam\",\n" +
                        "  \"surname\": \"Kocik\",\n" +
                        "  \"password\": \"12\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void createUserTestBlankNameShouldFail() throws  Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
                .content("{\n" +
                        "  \"login\": \"adam123\",\n" +
                        "  \"name\": \"\",\n" +
                        "  \"surname\": \"Kocik\",\n" +
                        "  \"password\": \"123\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }



    @Test
    public void shouldReturnListOfAllUsers() throws Exception {

        User newUser = new User("adam", "kocik", "adam123", "123");
        newUser.setId(1);
        Set<AbstractFigure> figuresSet = new HashSet<>();
        Square figure = new Square(BigDecimal.valueOf(80));
        figure.setId(1L);
        figure.setType("Square");
        figuresSet.add(figure);
        newUser.setCreatedFigures(figuresSet);
        Set<Role> roleSet = new HashSet<>();
        Role newRole = new Role("CREATOR");
        newRole.setId(1);
        roleSet.add(newRole);
        newUser.setRoles(roleSet);
        List<User> userList = new ArrayList<>();
        userList.add(newUser);
        UserDto dto = new UserDto();
        dto.setId(newUser.getId());
        RoleDto roleDto = new RoleDto();
        roleDto.setName(newRole.getName());
        roleDto.setId(newRole.getId());
        Set<RoleDto> roleDtoSet = new HashSet<>();
        roleDtoSet.add(roleDto);
        dto.setRoles(roleDtoSet);
        dto.setCreatedFigures(new HashSet<>());
        dto.setAmountOfCreatedFigures(0);
        List<UserDto> userDtoList = new ArrayList<>();
        userDtoList.add(dto);
        PageImpl<UserDto> userDtoPage = new PageImpl<>(userDtoList, PageRequest.of(5, 5), userDtoList.size());
        PageImpl<User> userPage = new PageImpl<>(userList, PageRequest.of(5, 5), userList.size());

        Mockito.when(userService.findAllUsers(ArgumentMatchers.isA(Pageable.class))).thenReturn(userPage);
        Mockito.when(userService.mapPageUsersToDto(ArgumentMatchers.any())).thenReturn(userDtoPage);


        this.mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());

    }



}