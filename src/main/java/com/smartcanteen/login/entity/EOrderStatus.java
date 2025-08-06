package com.smartcanteen.login.entity;

public enum EOrderStatus {

    PENDING,        // Order placed, awaiting canteen action
    PREPARING,      // Canteen has started preparing the order
    READY_FOR_PICKUP, // Order is ready for the student to pick up
    PICKED_UP,      // Order has been collected by the student
    CANCELLED
}
