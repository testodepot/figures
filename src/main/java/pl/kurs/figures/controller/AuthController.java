package pl.kurs.figures.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.figures.security.AuthRequest;
import pl.kurs.figures.security.AuthResponse;
import pl.kurs.figures.security.RefreshJwtAuthenticationResponse;
import pl.kurs.figures.service.AuthService;

import javax.validation.Valid;

@RestController
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody @Valid AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/auth/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader String refreshToken) throws Exception {
        RefreshJwtAuthenticationResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok().body(response);
    }




}
