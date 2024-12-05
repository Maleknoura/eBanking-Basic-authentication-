package org.wora.ebanking.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name="users")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role = UserRole.ROLE_USER;
}