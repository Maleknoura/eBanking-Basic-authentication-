package org.wora.ebanking.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserManagementController {
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public String registerUser() {
        return "User registered successfully";
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String getAllUsers() {
        return "List of all users";
    }

    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public String getUserByUsername(@PathVariable String username) {
        return "User details for " + username;
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable String username) {
        return "User " + username + " deleted";
    }

    @PutMapping("/{username}/updateRole")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUserRole(@PathVariable String username) {
        return "User role updated for " + username;
    }

    @PutMapping("/{username}")
    @PreAuthorize("hasRole('USER')")
    public String changePassword(@PathVariable String username) {
        return "Password changed for " + username;
    }
}
