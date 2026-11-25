package com.grocery.management.controller;

import com.grocery.management.dto.AuthDtos.JwtResponse;
import com.grocery.management.dto.AuthDtos.LoginRequest;
import com.grocery.management.dto.AuthDtos.RegisterRequest;
import com.grocery.management.dto.ApiResponse;
import com.grocery.management.entity.User;
import com.grocery.management.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(true, "User '" + user.getUsername() + "' registered successfully as " + user.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
