package com.smartcanteen.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for payment requests.
 * Contains fields for order ID, amount, and payment method.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    @NotNull(message = "Order ID cannot be null")
    private Long orderId; // The ID of the order being paid for

    @NotNull(message = "Amount cannot be null")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount; // The amount to be paid

    @NotNull(message = "Payment method cannot be null")
    private String paymentMethod; // The method of payment (e.g., credit card, PayPal)
    
    // You might add a field for transaction details if integrating with a gateway
}
