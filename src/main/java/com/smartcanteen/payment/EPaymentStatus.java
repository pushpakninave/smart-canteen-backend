package com.smartcanteen.payment;

public enum EPaymentStatus {
    PENDING,        // Payment initiated but not yet confirmed
    COMPLETED,      // Payment successful
    FAILED,         // Payment failed
    REFUNDED,       // Payment was refunded
    CANCELLED_BY_USER
}
