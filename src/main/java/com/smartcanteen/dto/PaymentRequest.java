// E:\Salman(29-06-2025)\smart-canteen-backend\src\main\java\com\smartcanteen\dto\PaymentRequest.java

package com.smartcanteen.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Order ID is required")
    private Long orderId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Payment method is required")
    private String paymentMethod; // e.g., "UPI", "Credit Card", "Cash"

    // You might add a field for transaction details if integrating with a gateway
}