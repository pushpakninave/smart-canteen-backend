package com.smartcanteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
// Ensure this annotation is present for the new constructor
public class FoodItemResponse {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean availableToday;
    private LocalDateTime donatedAt;
    private LocalDateTime receivedByNgoAt; // <<< NEW FIELD: To expose reception timestamp

    // --- AllArgsConstructor should generate this, but explicit for clarity ---
    public FoodItemResponse(Long id, String name, String description, BigDecimal price, boolean availableToday, LocalDateTime donatedAt, LocalDateTime receivedByNgoAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.availableToday = availableToday;
        this.donatedAt = donatedAt;
        this.receivedByNgoAt = receivedByNgoAt; // Assign new field
    }
}