package com.example.todo.service;

import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.repository.TaskRepository;
import com.example.todo.repository.UserRepository;
import com.example.todo.controller.requests.TaskRequest;
import com.example.todo.controller.responses.TaskResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public List<TaskResponse> getAllTasks() {
        User user = getCurrentUser();
        return taskRepository.findAllByUserId(user.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse createTask(TaskRequest request) {
        User user = getCurrentUser();
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(false);
        task.setUser(user);
        
        task = taskRepository.save(task);
        return mapToResponse(task);
    }

    public TaskResponse getTask(Long id) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));
        
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Доступ запрещен");
        }
        
        return mapToResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskRequest request) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));
        
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Доступ запрещен");
        }
        
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task = taskRepository.save(task);
        
        return mapToResponse(task);
    }

    public void deleteTask(Long id) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));
        
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Доступ запрещен");
        }
        
        taskRepository.delete(task);
    }

    public TaskResponse toggleTaskComplete(Long id) {
        User user = getCurrentUser();
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задача не найдена"));
        
        if (!task.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Доступ запрещен");
        }
        
        task.setCompleted(!task.isCompleted());
        task = taskRepository.save(task);
        
        return mapToResponse(task);
    }

    private TaskResponse mapToResponse(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.isCompleted()
        );
    }
} 