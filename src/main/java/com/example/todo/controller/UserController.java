package com.example.todo.controller;

import com.example.todo.controller.responses.UserResponse;
import com.example.todo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.todo.dto.UserUpdateDto;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        return ResponseEntity.ok(userService.getCurrentUser());
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@RequestBody UserUpdateDto updateDto) {
        return ResponseEntity.ok(userService.updateCurrentUser(updateDto));
    }
} 