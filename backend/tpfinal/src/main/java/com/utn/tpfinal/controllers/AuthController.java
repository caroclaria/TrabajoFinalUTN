package com.utn.tpfinal.controllers;

import com.utn.tpfinal.dtos.auth.LoginDto;
import com.utn.tpfinal.dtos.auth.LoginRequest;
import com.utn.tpfinal.dtos.auth.MeDto;
import com.utn.tpfinal.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public LoginDto login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    // Perfil del usuario logueado (se identifica por el token, no hace falta mandar nada)
    @GetMapping("/me")
    public MeDto me(Authentication authentication) {
        return authService.me(authentication.getName());
    }
}
