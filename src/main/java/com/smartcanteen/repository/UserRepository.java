package com.smartcanteen.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.smartcanteen.entity.Role;
import com.smartcanteen.entity.User;

public interface UserRepository extends JpaRepository<Object, Long>{
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // NEW: Custom method to count users by a specific role
    long countByRole(Role role);
}