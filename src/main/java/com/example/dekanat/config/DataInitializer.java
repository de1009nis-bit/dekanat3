package com.example.dekanat.config;

import com.example.dekanat.model.AppUser;
import com.example.dekanat.model.UserRole;
import com.example.dekanat.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner createDekanat(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            if (!userRepository.existsByUsername("dekanat")) {

                AppUser dekanat = new AppUser();

                dekanat.setUsername("dekanat");
                dekanat.setPassword(
                        passwordEncoder.encode("12345678")
                );
                dekanat.setRole(UserRole.DEKANAT);

                userRepository.save(dekanat);
            }
        };
    }
}