package com.smartcanteen.orderitems;

import com.smartcanteen.dto.OrderItemResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Data
@NoArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId; // The ID of the user who placed the order
    private String username; // The username of the user who placed the order
    private LocalDateTime orderDate;
    private BigDecimal totalPrice;
    private EOrderStatus status;
    private List<OrderItemResponse> items; // List of order items

    // Constructor for easy mapping
    public OrderResponse(Long id, Long userId, String username, LocalDateTime orderDate, BigDecimal totalPrice, EOrderStatus status, List<OrderItemResponse> items) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.items = items;
    }
}
