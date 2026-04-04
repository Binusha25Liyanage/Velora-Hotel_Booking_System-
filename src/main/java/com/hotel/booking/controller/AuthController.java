package com.hotel.booking.controller;

import com.hotel.booking.dto.AuthLoginRequest;
import com.hotel.booking.dto.AuthRegisterRequest;
import com.hotel.booking.dto.AuthResponse;
import com.hotel.booking.dto.RegisterResponse;
import com.hotel.booking.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody AuthRegisterRequest request) {
        return ResponseEntity.ok(authService.registerCustomer(request));
    }

    @PostMapping("/register/guide")
    public ResponseEntity<RegisterResponse> registerGuide(@Valid @RequestBody AuthRegisterRequest request) {
        return ResponseEntity.ok(authService.registerGuide(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthLoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
