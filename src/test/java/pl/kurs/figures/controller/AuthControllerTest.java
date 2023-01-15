package pl.kurs.figures.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import pl.kurs.figures.model.User;
import pl.kurs.figures.security.AuthRequest;
import pl.kurs.figures.security.AuthResponse;
import pl.kurs.figures.service.AuthService;
import pl.kurs.figures.utils.JwtTokenUtil;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private String token;

    private String refreshToken;

    @Autowired
    private AuthenticationManager authManager;



    @BeforeEach
    public void setUp() {
        User dummy = new User("adam", "kocik", "adam123", "123");
        token = jwtTokenUtil.generateAccessToken(dummy);
        Authentication authentication = authManager.authenticate(new UsernamePasswordAuthenticationToken(
                dummy.getLogin(), dummy.getPassword()));
        refreshToken = jwtTokenUtil.generateRefreshToken(authentication);
    }

    @Test
    public void loginTestShouldGenerateAccessTokenForUser() throws Exception {

        AuthResponse authResponse = new AuthResponse("adam123", token, refreshToken);
        Mockito.when(authService.login(ArgumentMatchers.isA(AuthRequest.class))).thenReturn(authResponse);

        this.mockMvc.perform(MockMvcRequestBuilders
                .post("/auth/login")
                .content("{\n" +
                        "  \"login\": \"adam123\",\n" +
                        "  \"password\": \"123\"\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andDo(mvcResult -> System.out.println(mvcResult.getResponse().getContentAsString()))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful());
    }

}