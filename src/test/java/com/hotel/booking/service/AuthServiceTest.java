package com.hotel.booking.service;

import com.hotel.booking.dto.AuthLoginRequest;
import com.hotel.booking.dto.AuthRegisterRequest;
import com.hotel.booking.dto.AuthResponse;
import com.hotel.booking.dto.RegisterResponse;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.UserRepository;
import com.hotel.booking.security.CustomUserDetailsService;
import com.hotel.booking.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerCustomer_hashesPasswordAndSetsUserRole() {
        AuthRegisterRequest request = new AuthRegisterRequest();
        request.setName("Alice");
        request.setEmail("alice@test.com");
        request.setPassword("plain123");

        when(userRepository.existsByEmail("alice@test.com")).thenReturn(false);
        when(passwordEncoder.encode("plain123")).thenReturn("hashed_pw");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("u-1");
            return u;
        });

        RegisterResponse response = authService.registerCustomer(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertEquals("hashed_pw", saved.getPassword());
        assertEquals("USER", saved.getRole());
        assertEquals("u-1", response.getUserId());
    }

    @Test
    void login_throwsWhenPasswordDoesNotMatch() {
        AuthLoginRequest request = new AuthLoginRequest();
        request.setEmail("alice@test.com");
        request.setPassword("wrong");

        User user = new User();
        user.setId("u-1");
        user.setEmail("alice@test.com");
        user.setPassword("hashed_pw");
        user.setRole("USER");

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed_pw")).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> authService.login(request));
    }

    @Test
    void login_returnsJwtOnValidCredentials() {
        AuthLoginRequest request = new AuthLoginRequest();
        request.setEmail("alice@test.com");
        request.setPassword("correct");

        User user = new User();
        user.setId("u-1");
        user.setName("Alice");
        user.setEmail("alice@test.com");
        user.setPassword("hashed_pw");
        user.setRole("USER");

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("alice@test.com")
                .password("hashed_pw")
                .roles("USER")
                .build();

        when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correct", "hashed_pw")).thenReturn(true);
        when(userDetailsService.loadUserByUsername("alice@test.com")).thenReturn(userDetails);
        when(jwtService.generateToken(eq(userDetails), eq("USER"))).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getToken());
        assertEquals("u-1", response.getUserId());
    }
}
