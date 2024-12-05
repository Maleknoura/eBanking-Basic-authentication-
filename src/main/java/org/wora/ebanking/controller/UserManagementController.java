package org.wora.ebanking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.wora.ebanking.entity.AppUser;
import org.wora.ebanking.entity.UserRole;
import org.wora.ebanking.exception.UsernameAlreadyExistsException;
import org.wora.ebanking.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/register")
    public ResponseEntity<String>  registerUser(@RequestBody AppUser newUser) {
        if (newUser.getUsername() == null || newUser.getPassword() == null) {
            throw new IllegalArgumentException("Username and password are required");
        }

        if (userRepository.existsByUsername(newUser.getUsername())) {
            throw new UsernameAlreadyExistsException("Username already exists");
        }

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        newUser.setRole(UserRole.ROLE_USER);

        userRepository.save(newUser);

        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<String> getAllUsers() {
        return userRepository.findAll().stream()
                .map(AppUser::getUsername)
                .collect(Collectors.toList());
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public String getUserByUsername(@PathVariable String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        return "User details: " + user.getUsername() + ", Role: " + user.getRole();
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable String username) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        userRepository.delete(user);
        return "User " + username + " deleted successfully";
    }

    @PutMapping("/{username}/updateRole")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUserRole(
            @PathVariable String username,
            @RequestBody UserRole newRole
    ) {
        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        user.setRole(newRole);
        userRepository.save(user);

        return "Role updated for user " + username + " to " + newRole;
    }
}

