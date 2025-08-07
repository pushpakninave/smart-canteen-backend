
package com.smartcanteen.controller;

import com.smartcanteen.dto.OrderRequest;
import com.smartcanteen.dto.OrderResponse;
import com.smartcanteen.login.enity.EOrderStatus;
import com.smartcanteen.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders") // Base path for all order-related endpoints
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // --- Endpoints for Students/Users (ROLE_STUDENT) ---

    /**
     * Endpoint to place a new order for the currently authenticated student.
     * POST /api/orders/place
     * @param orderRequest The DTO containing the list of food items and quantities.
     * @return The created OrderResponse DTO.
     */
    @PostMapping("/place")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest orderRequest) {
        OrderResponse newOrder = orderService.placeOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newOrder);
    }

    /**
     * Endpoint for students to view their own orders.
     * GET /api/orders/my
     * @return A list of OrderResponse DTOs for the current user.
     */
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<OrderResponse>> getMyOrders() {
        List<OrderResponse> myOrders = orderService.getMyOrders();
        return ResponseEntity.ok(myOrders);
    }

    /**
     * Endpoint for students to view details of a specific order.
     * GET /api/orders/my/{orderId}
     * @param orderId The ID of the order to retrieve.
     * @return The OrderResponse DTO for the specified order.
     */
    @GetMapping("/my/{orderId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<OrderResponse> getMyOrderById(@PathVariable Long orderId) {
        OrderResponse order = orderService.getMyOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Endpoint for students to cancel their own order.
     * PUT /api/orders/my/cancel/{orderId}
     * @param orderId The ID of the order to cancel.
     * @return ResponseEntity with no content.
     */
    @PutMapping("/my/cancel/{orderId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Void> cancelMyOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId, false); // false indicates not a manager
        return ResponseEntity.noContent().build();
    }

    // --- Endpoints for Canteen Managers/Admins (ROLE_CANTEEN_MANAGER, ROLE_ADMIN) ---

    /**
     * Endpoint for managers/admins to view all orders.
     * GET /api/orders/all
     * @return A list of all OrderResponse DTOs.
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('CANTEEN_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> allOrders = orderService.getAllOrders();
        return ResponseEntity.ok(allOrders);
    }

    /**
     * Endpoint for managers/admins to view details of a specific order.
     * GET /api/orders/details/{orderId} <<< CRITICAL FIX: Changed path to avoid conflict
     * @param orderId The ID of the order to retrieve.
     * @return The OrderResponse DTO for the specified order.
     */
    @GetMapping("/details/{orderId}") // <<< CRITICAL FIX: Changed path to avoid conflict
    @PreAuthorize("hasRole('CANTEEN_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    /**
     * Endpoint for managers/admins to update the status of an order.
     * PUT /api/orders/{orderId}/status?newStatus=...
     * @param orderId The ID of the order to update.
     * @param newStatus The new status for the order.
     * @return The updated OrderResponse DTO.
     */
    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('CANTEEN_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam EOrderStatus newStatus) {
        OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * Endpoint for managers/admins to cancel any order.
     * PUT /api/orders/cancel/{orderId}
     * @param orderId The ID of the order to cancel.
     * @return ResponseEntity with no content.
     */
    @PutMapping("/cancel/{orderId}")
    @PreAuthorize("hasRole('CANTEEN_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<Void> cancelOrderByManager(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId, true); // true indicates manager
        return ResponseEntity.noContent().build();
    }
}