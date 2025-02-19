package com.example.todo.service;

import com.example.todo.dto.UserRegistrationDto;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.example.todo.exception.UserAlreadyExistsException;
import com.example.todo.exception.InvalidCredentialsException;
import com.example.todo.controller.requests.LoginRequest;
import com.example.todo.controller.responses.TokenResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import com.example.todo.dto.UserUpdateDto;
import com.example.todo.controller.responses.UserResponse;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public User registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с email " + registrationDto.getEmail() + " уже существует");
        }

        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setHashedPassword(passwordEncoder.encode(registrationDto.getPassword()));

        return userRepository.save(user);
    }

    public TokenResponse login(LoginRequest loginDto) {
        User user = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Неверный email или пароль"));

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getHashedPassword())) {
            throw new InvalidCredentialsException("Неверный email или пароль");
        }

        String accessToken = jwtService.generateAccessToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        return new TokenResponse(accessToken, refreshToken);
    }

    public UserResponse getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        return new UserResponse(user.getId(), user.getEmail());
    }

    public UserResponse updateCurrentUser(UserUpdateDto updateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (updateDto.getEmail() != null && !updateDto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updateDto.getEmail())) {
                throw new UserAlreadyExistsException("Email уже занят");
            }
            user.setEmail(updateDto.getEmail());
        }

        user = userRepository.save(user);
        return new UserResponse(user.getId(), user.getEmail());
    }

    public TokenResponse refreshToken(String refreshToken) {
        String email = jwtService.extractEmail(refreshToken);
        if (email == null) {
            throw new InvalidCredentialsException("Невалидный refresh token");
        }

        userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Пользователь не найден"));

        String newAccessToken = jwtService.generateAccessToken(email);
        String newRefreshToken = jwtService.generateRefreshToken(email);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
} 