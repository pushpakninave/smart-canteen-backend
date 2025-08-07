package com.smartcanteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private String role; // Simplified to a single role name for display
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
