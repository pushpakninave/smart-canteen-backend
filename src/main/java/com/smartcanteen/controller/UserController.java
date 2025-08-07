// E:\Salman(29-06-2025)\smart-canteen-backend\src\main\java\com\smartcanteen\controller\UserController.java

package com.smartcanteen.controller;

import com.smartcanteen.dto.UserResponse;
import com.smartcanteen.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users") // Base path for user management
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all users. Only accessible by ADMIN.
     * GET /api/users/all
     * @return List of UserResponse DTOs.
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Get a specific user by ID. Only accessible by ADMIN.
     * GET /api/users/{userId}
     * @param userId The ID of the user to retrieve.
     * @return The UserResponse DTO.
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    /**
     * Update a user's role. Only accessible by ADMIN.
     * PUT /api/users/{userId}/role?newRole=...
     * @param userId The ID of the user whose role is to be updated.
     * @param newRole The new role name (e.g., "STUDENT", "CANTEEN_MANAGER", "ADMIN").
     * @return The updated UserResponse DTO.
     */
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long userId,
            @RequestParam String newRole) {
        UserResponse updatedUser = userService.updateUserRole(userId, newRole);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Delete a user. Only accessible by ADMIN.
     * DELETE /api/users/{userId}
     * @param userId The ID of the user to delete.
     * @return ResponseEntity with no content.
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}