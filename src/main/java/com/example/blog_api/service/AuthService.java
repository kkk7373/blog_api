package com.example.blog_api.service;

import com.example.blog_api.dto.LoginRequest;
import com.example.blog_api.dto.LoginResponse;
import com.example.blog_api.dto.User;
import com.example.blog_api.exception.InvalidCredentialsException;
import com.example.blog_api.repository.UserRepository;
import com.example.blog_api.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByName(request.getName())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String token = jwtUtil.generateToken(user.getName(), user.getId());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserId(user.getId());

        return response;
    }
}
