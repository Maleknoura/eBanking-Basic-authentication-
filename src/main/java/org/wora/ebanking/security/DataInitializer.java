package org.wora.ebanking.security;

import lombok.RequiredArgsConstructor;
import org.wora.ebanking.entity.AppUser;
import org.wora.ebanking.entity.UserRole;
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
                AppUser regularUser = new AppUser();
                regularUser.setUsername("user");
                regularUser.setPassword(passwordEncoder.encode("password"));
                regularUser.setRole(UserRole.ROLE_USER);
                userRepository.save(regularUser);

                AppUser adminUser = new AppUser();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin"));
                adminUser.setRole(UserRole.ROLE_ADMIN);
                userRepository.save(adminUser);
            }
        };
    }
}

