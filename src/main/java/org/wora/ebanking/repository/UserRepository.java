package org.wora.ebanking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.wora.ebanking.entity.AppUser;

import java.util.Optional;


public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
