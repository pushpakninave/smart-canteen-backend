package com.smartcanteen.login.entity;

public enum EPaymentStatus {
     PENDING,    // Payment initiated but not yet confirmed
    COMPLETED,  // Payment successfully processed
    FAILED,     // Payment attempt failed
    REFUNDED,   // Payment has been refunded
    CANCELLED   // Payment cancelled (e.g., associated order cancelled before payment)

}
