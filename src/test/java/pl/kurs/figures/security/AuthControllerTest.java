package pl.kurs.figures.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

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


    @BeforeEach
    public void setUp() {
        User dummy = new User("adam", "kocik", "adam123", "123");
        token = jwtTokenUtil.generateAccessToken(dummy);
    }

    @Test
    public void loginTestShouldGenerateAccessTokenForUser() throws Exception {

        AuthResponse authResponse = new AuthResponse("adam123", token);
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