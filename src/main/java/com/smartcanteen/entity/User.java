package com.smartcanteen.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Entity;
import lombok.Builder;
import lombok.Data;

@Entity
@Builder
@Data
public class User {
    private Long id;
    private String userName;
    private String email;
    private String password;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Order> orders;
}