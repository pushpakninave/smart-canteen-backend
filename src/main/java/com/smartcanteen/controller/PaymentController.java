
package com.smartcanteen.controller;

import com.smartcanteen.dto.PaymentRequest;
import com.smartcanteen.dto.PaymentResponse;
import com.smartcanteen.login.enity.EPaymentStatus; // Import the payment status enum
import com.smartcanteen.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments") // Base path for all payment-related endpoints
public class PaymentController {

    private final PaymentService paymentService; // Inject the PaymentService

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // --- Endpoints for Students/Users (ROLE_STUDENT) ---

    /**
     * Endpoint for students to process a payment for an order.
     * @param paymentRequest The request body containing orderId, amount, and paymentMethod.
     * @return The created PaymentResponse DTO.
     */
    @PostMapping("/process") // e.g., POST /api/payments/process
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        PaymentResponse newPayment = paymentService.processPayment(paymentRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPayment);
    }

    /**
     * Endpoint for students to view their own payment history.
     * @return A list of PaymentResponse DTOs for the current user.
     */
    @GetMapping("/my") // e.g., GET /api/payments/my
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<PaymentResponse>> getMyPayments() {
        List<PaymentResponse> myPayments = paymentService.getMyPayments();
        return ResponseEntity.ok(myPayments);
    }

    /**
     * Endpoint for students to view details of a specific payment.
     * @param paymentId The ID of the payment to retrieve.
     * @return The PaymentResponse DTO for the specified payment.
     */
    @GetMapping("/my/{paymentId}") // e.g., GET /api/payments/my/123
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PaymentResponse> getMyPaymentById(@PathVariable Long paymentId) {
        PaymentResponse payment = paymentService.getMyPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    // --- Endpoints for Canteen Managers/Admins (ROLE_CANTEEN_MANAGER, ROLE_ADMIN) ---

    /**
     * Endpoint for managers/admins to view all payment records.
     * @return A list of all PaymentResponse DTOs.
     */
    @GetMapping("/all") // e.g., GET /api/payments/all
    @PreAuthorize("hasRole('CANTEEN_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> allPayments = paymentService.getAllPayments();
        return ResponseEntity.ok(allPayments);
    }

    /**
     * Endpoint for managers/admins to view details of a specific payment.
     * @param paymentId The ID of the payment to retrieve.
     * @return The PaymentResponse DTO for the specified payment.
     */
    @GetMapping("/{paymentId}") // e.g., GET /api/payments/123
    @PreAuthorize("hasRole('CANTEEN_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long paymentId) {
        PaymentResponse payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(payment);
    }

    /**
     * Endpoint for managers/admins to update the status of a payment.
     * @param paymentId The ID of the payment to update.
     * @param newStatus The new status for the payment (e.g., COMPLETED, REFUNDED).
     * @return The updated PaymentResponse DTO.
     */
    @PutMapping("/{paymentId}/status") // e.g., PUT /api/payments/123/status?newStatus=REFUNDED
    @PreAuthorize("hasRole('CANTEEN_MANAGER') or hasRole('ADMIN')")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long paymentId,
            @RequestParam EPaymentStatus newStatus) {
        PaymentResponse updatedPayment = paymentService.updatePaymentStatus(paymentId, newStatus);
        return ResponseEntity.ok(updatedPayment);
    }
}