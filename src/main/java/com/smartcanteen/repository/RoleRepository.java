package com.smartcanteen.repository;

import com.smartcanteen.login.enity.ERole;
import com.smartcanteen.security.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    // Custom method to find a role by its name (e.g., "STUDENT")
    Optional<Role> findByName(ERole name);
}
