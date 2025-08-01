package com.smartcanteen.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal; // For precise currency handling

@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ensure the order and foodItem fields are correctly mapped to their respective entities
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_item_id", nullable = false)
    private Fooditem foodItem; // Ensure this is FoodItem, not Fooditem

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2) // Price of the item at the time of order
    private BigDecimal priceAtOrder; // Renamed to avoid confusion with subtotal

    @Column(nullable = false, precision = 10, scale = 2) // Subtotal for this specific item (price * quantity)
    private BigDecimal subtotal;
    
    public void setOrder(Order order) {
        this.order = order;
    }
	
}
    