// E:\Salman(29-06-2025)\smart-canteen-backend\src\main\java\com\smartcanteen\security\Role.java

package com.smartcanteen.security;

import com.smartcanteen.login.enity.ERole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor; // <<< Make sure this line is NOT commented out
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data

@AllArgsConstructor // <<< This annotation should be active
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, unique = true, nullable = false)
    private ERole name;

    // Explicit no-argument constructor (kept for robustness, though @NoArgsConstructor usually handles it)
    public Role() {
        // Default constructor logic if any, otherwise can be empty
    }

    // Explicit constructor for (name) if your DataInitializer uses it
    // This is still needed because @AllArgsConstructor only creates a constructor with *all* fields (id, name)
    public Role(ERole name) {
        this.name = name;
    }

    // Explicitly defined getters and setters (Lombok's @Data also generates these, but explicit is safer here)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ERole getName() {
        return name;
    }

    public void setName(ERole name) {
        this.name = name;
    }
}