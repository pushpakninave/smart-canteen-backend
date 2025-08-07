// E:\Salman(29-06-2025)\smart-canteen-backend\src\main\java\com\smartcanteen\repository\UserRepository.java

package com.smartcanteen.repository;

import com.smartcanteen.security.Role; // Import Role entity
import com.smartcanteen.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);

    // <<< NEW: Custom method to count users by a specific role >>>
    long countByRole(Role role);
}