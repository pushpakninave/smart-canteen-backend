
package com.smartcanteen.dto;

import com.smartcanteen.login.enity.EOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String username;
    private BigDecimal totalPrice; // Total price of the order
    private EOrderStatus status; // Current status of the order
    private List<OrderItemResponse> orderItems;
}