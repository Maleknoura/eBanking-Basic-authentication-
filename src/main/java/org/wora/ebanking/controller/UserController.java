package org.wora.ebanking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.wora.ebanking.dto.PasswordChangeRequest;
import org.wora.ebanking.entity.AppUser;
import org.wora.ebanking.repository.UserRepository;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/myLoans")
    @PreAuthorize("hasRole('USER')")
    public String getUserLoans() {
        return "User's loan information";
    }

    @GetMapping("/myCards")
    @PreAuthorize("hasRole('USER')")
    public String getUserCards() {
        return "User's bank cards details";
    }

    @GetMapping("/myAccount")
    @PreAuthorize("hasRole('USER')")
    public String getUserAccount() {
        return "User's account information";
    }

    @GetMapping("/myBalance")
    @PreAuthorize("hasRole('USER')")
    public String getUserBalance() {
        return "User's total account balance";
    }


    @PutMapping("/{username}")
    public ResponseEntity<String> changePassword(
            @PathVariable String username,
            @RequestBody PasswordChangeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (!userDetails.getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        AppUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid old password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }
}

