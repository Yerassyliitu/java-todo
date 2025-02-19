package com.example.todo.controller;

import com.example.todo.dto.UserRegistrationDto;
import com.example.todo.controller.responses.TokenResponse;
import com.example.todo.controller.responses.UserResponse;
import com.example.todo.controller.requests.LoginRequest;
import com.example.todo.controller.requests.RefreshTokenRequest;
import com.example.todo.entity.User;
import com.example.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        User user = userService.registerUser(registrationDto);
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginDto) {
        return ResponseEntity.ok(userService.login(loginDto));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(userService.refreshToken(refreshTokenRequest.getRefreshToken()));
    }
} 