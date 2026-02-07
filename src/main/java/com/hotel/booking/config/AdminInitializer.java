package com.hotel.booking.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.UserRepository;

@Component
public class AdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdmin() {

        if (!userRepository.existsByEmail("admin@staysphere.com")) {

            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@staysphere.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");

            userRepository.save(admin);
        }
    }
}
