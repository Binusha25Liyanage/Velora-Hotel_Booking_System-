package com.hotel.booking.service;

import com.hotel.booking.dto.AuthLoginRequest;
import com.hotel.booking.dto.AuthRegisterRequest;
import com.hotel.booking.dto.AuthResponse;
import com.hotel.booking.dto.RegisterResponse;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.UserRepository;
import com.hotel.booking.security.CustomUserDetailsService;
import com.hotel.booking.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       CustomUserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    public RegisterResponse registerCustomer(AuthRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("USER");

        User saved = userRepository.save(user);
        return new RegisterResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
    }

    public RegisterResponse registerGuide(AuthRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists");
        }

        User guide = new User();
        guide.setName(request.getName());
        guide.setEmail(request.getEmail());
        guide.setPassword(passwordEncoder.encode(request.getPassword()));
        guide.setRole("GUIDE");

        User saved = userRepository.save(guide);
        return new RegisterResponse(saved.getId(), saved.getName(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(AuthLoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(userDetails, user.getRole());
        return new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
