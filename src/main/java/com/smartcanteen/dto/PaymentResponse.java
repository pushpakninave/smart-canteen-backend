
package com.smartcanteen.dto;

import com.smartcanteen.login.enity.EPaymentStatus; // Import EPaymentStatus
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long userId;
    private String username;
    private Long orderId;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private EPaymentStatus status;
    private String paymentMethod;
}