// E:\Salman(29-06-2025)\smart-canteen-backend\src\main\java\com\smartcanteen\login\enity\EPaymentStatus.java

package com.smartcanteen.login.enity;

public enum EPaymentStatus {
    PENDING,    // Payment initiated but not yet confirmed
    COMPLETED,  // Payment successfully processed
    FAILED,     // Payment attempt failed
    REFUNDED,   // Payment has been refunded
    CANCELLED   // Payment cancelled (e.g., associated order cancelled before payment)
}