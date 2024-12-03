package org.wora.ebanking.security;

import lombok.RequiredArgsConstructor;
import org.wora.ebanking.entity.AppUser;
import org.wora.ebanking.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner init() {
        return args -> {
            if (userRepository.count() == 0) {
                AppUser dbUser = new AppUser();
                dbUser.setUsername("dbuser");
                dbUser.setPassword(passwordEncoder.encode("password"));
                userRepository.save(dbUser);
            }
        };
    }
}