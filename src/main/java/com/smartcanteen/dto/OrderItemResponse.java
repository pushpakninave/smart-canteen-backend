package com.smartcanteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long id;
    private Long foodItemId;
    private String foodItemName;
    private BigDecimal foodItemPrice; // Price at the time of order
    private Integer quantity;
    private BigDecimal subtotal;
}
