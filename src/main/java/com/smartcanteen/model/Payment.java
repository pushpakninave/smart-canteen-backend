package com.smartcanteen.model;

import com.smartcanteen.security.User;
import com.smartcanteen.login.entity.EPaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the payment

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who made the payment

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order; // The order associated with the payment

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount; // The amount paid

    @CreationTimestamp
    @Column(name = "payment_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime paymentDate; // The date and time when the payment was made

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false,length = 20)
    private EPaymentStatus status; // The status of the payment

    @Column(name = "payment_method", nullable = false, length = 50)
    private String paymentMethod; // The method used for the payment (e.g., credit card, PayPal)

    
}
