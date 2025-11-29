package com.example.blog_api.controller;

import com.example.blog_api.dto.LoginRequest;
import com.example.blog_api.dto.LoginResponse;
import com.example.blog_api.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthContoroller {

    private final AuthService authService;

    public AuthContoroller(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
