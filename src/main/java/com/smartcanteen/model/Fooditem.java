FoodItem Entity
package com.smartcanteen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "food_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fooditem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "available_today", nullable = false)
    private boolean availableToday = true;

    private LocalDateTime donatedAt; // Existing field for donation timestamp

    private LocalDateTime receivedByNgoAt; // <<< NEW FIELD: To track when NGO received the item

    // --- Existing Getters and Setters (Lombok @Data handles these) ---
    // public Long getId() { ... }
    // public void setId(Long id) { ... }
    // ... (other getters/setters)

    // --- NEW Getters and Setters for receivedByNgoAt (Lombok @Data should generate these) ---
    // public LocalDateTime getReceivedByNgoAt() { ... }
    // public void setReceivedByNgoAt(LocalDateTime receivedByNgoAt) { ... }
}