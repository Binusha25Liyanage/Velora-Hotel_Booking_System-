package com.hotel.booking.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.hotel.booking.model.User;
import com.hotel.booking.repository.UserRepository;

import java.util.Optional;

@Component
public class AdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdmin() {
        Optional<User> existingAdmin = userRepository.findByEmail("admin@staysphere.com");
        if (existingAdmin.isEmpty()) {
            User admin = new User();
            admin.setName("System Admin");
            admin.setEmail("admin@staysphere.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ADMIN");
            userRepository.save(admin);
            return;
        }

        User admin = existingAdmin.get();
        if (admin.getPassword() != null && !admin.getPassword().startsWith("$2")) {
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
            userRepository.save(admin);
        }
    }
}
