package com.Reserveit.v1.repository;

import com.Reserveit.v1.entity.Role;
import com.Reserveit.v1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByRole(Role role);

    long countByActiveTrue();

    // Keep this only if other parts of your project already use it.
    // Otherwise countByActiveTrue() is more efficient.
    java.util.List<User> findByActiveTrue();
}