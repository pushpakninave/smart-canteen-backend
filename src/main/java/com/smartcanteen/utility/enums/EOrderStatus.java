package com.smartcanteen.utility.enums;

public enum EOrderStatus {
    PENDING, // Order just placed, awaiting canteen action
    PREPARING, // Canteen is preparing the order
    READY_FOR_PICKUP, // Order is ready, waiting for student to pick up
    PICKED_UP, // Student has collected the order
    CANCELLED
}