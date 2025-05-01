package com.example.animalhosreviewadmin.controller;

import com.example.animalhosreviewadmin.domain.User;
import com.example.animalhosreviewadmin.service.AuthService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        // 비밀번호 유효성 검사
        if (registerRequest.getPassword().length() < 8) {
            return ResponseEntity.badRequest().body("비밀번호는 8자 이상이어야 합니다.");
        }

        if (!registerRequest.getPassword().matches(".*[A-Z].*")) {
            return ResponseEntity.badRequest().body("비밀번호는 대문자를 포함해야 합니다.");
        }

        if (!registerRequest.getPassword().matches(".*[0-9].*")) {
            return ResponseEntity.badRequest().body("비밀번호는 숫자를 포함해야 합니다.");
        }

        if (!registerRequest.getPassword().matches(".*[!@#$%^&*].*")) {
            return ResponseEntity.badRequest().body("비밀번호는 특수문자(!@#$%^&*)를 포함해야 합니다.");
        }

        try {
            User user = authService.register(registerRequest.getEmail(), registerRequest.getPassword());
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Data
    public static class LoginRequest {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        private String password;
    }

    @Data
    public static class RegisterRequest {
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
        private String password;
    }

    @Data
    public static class JwtAuthenticationResponse {
        private String token;

        public JwtAuthenticationResponse(String token) {
            this.token = token;
        }
    }
} 