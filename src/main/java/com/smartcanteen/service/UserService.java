package com.smartcanteen.service;

import com.smartcanteen.dto.UserResponse;
import com.smartcanteen.exception.ResourceNotFoundException;
import com.smartcanteen.login.enity.ERole;
import com.smartcanteen.repository.RoleRepository;
import com.smartcanteen.repository.UserRepository;
import com.smartcanteen.security.Role;
import com.smartcanteen.security.User;
import com.smartcanteen.security.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getId();
        } else if (principal instanceof String) {
            User user = userRepository.findByUsername((String) principal)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found for authenticated principal: " + principal));
            return user.getId();
        }
        throw new IllegalStateException("Authenticated principal is not of expected type.");
    }

    private UserResponse mapToUserResponse(User user) {
        // role.getName().name() will now return "ROLE_STUDENT", "ROLE_NGO" etc.
        String roleName = user.getRole() != null ? user.getRole().getName().name() : "UNKNOWN_ROLE";
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roleName,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    @Transactional
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUserRole(Long userId, String newRoleName) {
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (userToUpdate.getId().equals(getCurrentUserId())) {
            throw new IllegalArgumentException("You cannot change your own role.");
        }

        // Use ERole.fromName() for robust conversion, it will convert "STUDENT" or "ROLE_STUDENT" to ERole.ROLE_STUDENT
        ERole eRole = ERole.fromName(newRoleName);
        Role newRole = roleRepository.findByName(eRole)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + newRoleName));

        userToUpdate.setRole(newRole);
        userToUpdate.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(userToUpdate);
        return mapToUserResponse(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User userToDelete = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (userToDelete.getId().equals(getCurrentUserId())) {
            throw new IllegalArgumentException("You cannot delete your own account.");
        }

        // Use ERole.ROLE_ADMIN now that enum constants are prefixed
        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Admin role not found"));

        long adminCount = userRepository.countByRole(adminRole);
        if (userToDelete.getRole().equals(adminRole) && adminCount <= 1) {
            throw new IllegalArgumentException("Cannot delete the last admin user.");
        }

        userRepository.delete(userToDelete);
    }
}