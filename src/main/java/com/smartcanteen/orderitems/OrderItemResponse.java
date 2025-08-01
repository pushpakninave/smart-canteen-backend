//package com.smartcanteen.orderitems;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class OrderItemResponse {
//    private Long id; // ID of the OrderItem itself
//    private Long foodItemId;
//    private String foodItemName;
//    private BigDecimal pricePerItem; // Price of the food item when ordered
//    private Integer quantity;
//    private BigDecimal subtotal; // pricePerItem * quantity
//
//    // Constructor for easy mapping
//    public OrderItemResponse(Long id, Long foodItemId, String foodItemName, BigDecimal pricePerItem, Integer quantity, BigDecimal subtotal) {
//        this.id = id;
//        this.foodItemId = foodItemId;
//        this.foodItemName = foodItemName;
//        this.pricePerItem = pricePerItem;
//        this.quantity = quantity;
//        this.subtotal = subtotal;
//    }
//
//    // Getters for all fields
//    public Long getId() {
//        return id;
//    }
//
//    public Long getFoodItemId() {
//        return foodItemId;
//    }
//
//    public String getFoodItemName() {
//        return foodItemName;
//    }
//
//    public BigDecimal getPricePerItem() {
//        return pricePerItem;
//    }
//
//    public Integer getQuantity() {
//        return quantity;
//    }
//
//    public BigDecimal getSubtotal() {
//        return subtotal;
//    }
//
//    // No setters needed for response DTOs as they are typically immutable
//}
