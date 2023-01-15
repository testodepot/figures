package pl.kurs.figures.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import pl.kurs.figures.model.User;
import pl.kurs.figures.security.AuthRequest;
import pl.kurs.figures.security.AuthResponse;
import pl.kurs.figures.security.RefreshJwtAuthenticationResponse;
import pl.kurs.figures.security.RequestHandler;
import pl.kurs.figures.utils.JwtTokenUtil;

@Service
public class AuthService {

    private AuthenticationManager authManager;

    private JwtTokenUtil jwtUtil;

    private RequestHandler requestHandler;

    private UserService userService;

    public AuthService(AuthenticationManager authManager, JwtTokenUtil jwtUtil, RequestHandler requestHandler, UserService userService) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.requestHandler = requestHandler;
        this.userService = userService;
    }

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getLogin(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(authentication);
        AuthResponse response = new AuthResponse(user.getLogin(), accessToken, refreshToken);
        return response;
    }

    public RefreshJwtAuthenticationResponse refreshToken(String authRefreshToken) {
        String refreshJwt = requestHandler.getJwtFromStringRequest(authRefreshToken);
        String login = jwtUtil.getUserNameFromJWT(refreshJwt);
        User byLogin = userService.findByLogin(login);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(byLogin, byLogin.getPassword(), byLogin.getAuthorities());
        String accessJwtToken = jwtUtil.generateAccessToken((User) authentication.getPrincipal());
        RefreshJwtAuthenticationResponse response = new RefreshJwtAuthenticationResponse(accessJwtToken);
        return response;
    }
}
