package pl.kurs.figures.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import pl.kurs.figures.model.Role;
import pl.kurs.figures.model.User;
import pl.kurs.figures.security.AuthRequest;
import pl.kurs.figures.security.AuthResponse;
import pl.kurs.figures.security.RefreshJwtAuthenticationResponse;
import pl.kurs.figures.security.RequestHandler;
import pl.kurs.figures.utils.JwtTokenUtil;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private AuthService authService;

    @Mock
    private RequestHandler requestHandler;

    @Mock
    private UserService userService;



    @BeforeEach
    public void setup() {
        jwtTokenUtil = mock(JwtTokenUtil.class);
        authenticationManager = mock(AuthenticationManager.class);
        userService = mock(UserService.class);
        requestHandler = mock(RequestHandler.class);
        authService = new AuthService(authenticationManager, jwtTokenUtil, requestHandler, userService);
    }


    @Test
    public void loginTest() {

        Authentication authentication = Mockito.mock(Authentication.class);
        authentication.setAuthenticated(true);

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);

        AuthRequest authRequest = new AuthRequest("adam123", "123");

        when(authentication.isAuthenticated()).thenReturn(true);

        given(authenticationManager.authenticate(ArgumentMatchers.any())).willReturn(authentication);

        given(jwtTokenUtil.generateAccessToken(dummy)).willReturn("asfsdghdfrgjdghjfh");

        given(authentication.getPrincipal()).willReturn(dummy);

        AuthResponse authResponse = authService.login(authRequest);

        assertThat(authResponse).isNotNull();
    }


    @Test
    public void refreshTokenTest() {

        Authentication authentication = Mockito.mock(Authentication.class);
        authentication.setAuthenticated(true);

        User dummy = new User("adam", "kocik", "adam123", "123");
        Set<Role> userRoleSet = new HashSet<>();
        Role userNewRole = new Role("CREATOR");
        userNewRole.setId(1);
        userRoleSet.add(userNewRole);
        dummy.setRoles(userRoleSet);

        when(authentication.isAuthenticated()).thenReturn(true);

        given(authentication.getPrincipal()).willReturn(dummy);
        given(requestHandler.getJwtFromStringRequest(ArgumentMatchers.anyString())).willReturn("adasfdfhdfsdghdsfh");
        given(jwtTokenUtil.getUserNameFromJWT(ArgumentMatchers.anyString())).willReturn("adam123");
        given(userService.findByLogin(ArgumentMatchers.anyString())).willReturn(dummy);
        given(jwtTokenUtil.generateAccessToken(dummy)).willReturn("asfsdghdfrgjdghjfh");


        RefreshJwtAuthenticationResponse authResponse = authService.refreshToken("adasfdfhdfsdghdsfh");

        assertThat(authResponse).isNotNull();
    }
}