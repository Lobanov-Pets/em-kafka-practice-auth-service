package by.lobanov.authservice.controller;

import by.lobanov.authservice.dto.*;
import by.lobanov.authservice.service.*;
import lombok.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request.email());
        return ResponseEntity.ok("Confirmation code sent. Please check your 'email'.");
    }

    @PostMapping("/confirm")
    public ResponseEntity<AuthResponse> confirm(@RequestBody ConfirmRequest request) {
        String token = authService.confirm(request.email(), request.code());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}