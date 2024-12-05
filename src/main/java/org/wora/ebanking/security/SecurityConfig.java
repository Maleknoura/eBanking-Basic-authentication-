package org.wora.ebanking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import org.wora.ebanking.entity.AppUser;
import org.wora.ebanking.exception.CustomAccessDeniedHandler;
import org.wora.ebanking.repository.UserRepository;

import javax.sql.DataSource;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserRepository userRepository;
    private final DataSource dataSource;
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(
            UserRepository userRepository,
            DataSource dataSource,
            CustomUserDetailsService customUserDetailsService
    ) {
        this.userRepository = userRepository;
        this.dataSource = dataSource;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .httpBasic(Customizer.withDefaults())

                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/users/register").permitAll()

                        .requestMatchers("/api/notices", "/api/contact").permitAll()

                        .requestMatchers("/h2-console/**", "/public/**", "/error").permitAll()

                        .requestMatchers("/api/myLoans", "/api/myCards",
                                "/api/myAccount", "/api/myBalance")
                        .hasRole("USER")

                        .requestMatchers("/api/users/**")
                        .hasRole("ADMIN")

                        .anyRequest().authenticated()
                )

                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                )

                .csrf(csrf -> csrf.disable())

                .formLogin(form -> form.disable())

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )

                .authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();

        UserDetails memoryUser = User.builder()
                .username("memoryuser")
                .password(passwordEncoder().encode("memorypassword"))
                .roles("USER")
                .build();
        inMemoryUserDetailsManager.createUser(memoryUser);

        userRepository.findAll().forEach(user -> {
            UserDetails dbUser = User.builder()
                    .username(user.getUsername())
                    .password(user.getPassword())
                    .roles(user.getRole().name().replace("ROLE_", ""))
                    .build();
            jdbcUserDetailsManager.createUser(dbUser);
        });

        return username -> {
            try {
                return inMemoryUserDetailsManager.loadUserByUsername(username);
            } catch (Exception inMemoryEx) {
                try {
                    return jdbcUserDetailsManager.loadUserByUsername(username);
                } catch (Exception jdbcEx) {
                    Optional<AppUser> repoUser = userRepository.findByUsername(username);

                    return repoUser.map(user -> User.builder()
                                    .username(user.getUsername())
                                    .password(user.getPassword())
                                    .roles(user.getRole().name().replace("ROLE_", ""))
                                    .build())
                            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
                }
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
}