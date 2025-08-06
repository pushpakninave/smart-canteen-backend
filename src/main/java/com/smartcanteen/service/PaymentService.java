package com.smartcanteen.service;

import com.smartcanteen.dto.PaymentRequest; // <<< CORRECTED IMPORT
import com.smartcanteen.dto.PaymentResponse;
import com.smartcanteen.login.entity.EOrderStatus; // For checking order status
import com.smartcanteen.login.entity.EPaymentStatus; // <<< CORRECTED IMPORT
import com.smartcanteen.exception.ResourceNotFoundException; // <<< CORRECTED IMPORT
import com.smartcanteen.model.Order;
import com.smartcanteen.model.Payment; // <<< CORRECTED IMPORT
import com.smartcanteen.repository.OrderRepository;
import com.smartcanteen.repository.PaymentRepository;
import com.smartcanteen.repository.UserRepository;
import com.smartcanteen.security.User;
import com.smartcanteen.security.UserDetailsImpl; // Corrected import
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal; // For BigDecimal
import java.time.LocalDateTime; // For LocalDateTime
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository; 
    private final UserRepository userRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User not authenticated.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getId();
        } else if (principal instanceof String) {
            User user = userRepository.findByUsername((String) principal)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found for authenticated principal: " + principal));
            return user.getId();
        }

        throw new IllegalStateException("Authenticated principal is not of expected type.");
    }
    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getUser().getId(), // userId
                payment.getUser().getUsername(), // username
                payment.getOrder().getId(), // orderId
                payment.getAmount(),
                payment.getPaymentDate(), // paymentDate from entity
                payment.getStatus(),
                payment.getPaymentMethod() // paymentMethod from entity
        );
    }


    @Transactional
    public PaymentResponse processPayment(PaymentRequest paymentRequest) {
        Long currentUserId = getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated User not found with id: " + currentUserId));
        
        Order order = orderRepository.findById(paymentRequest.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + paymentRequest.getOrderId()));
        
        if(!order.getUser().getId().equals(currentUserId)) {
            throw new IllegalStateException("Order does not belong to the authenticated user.");
            }

        if(paymentRepository.findByOrderId(order.getId()).isPresent()) {
            throw new IllegalStateException("Payment already exists for order ID: " + order.getId());
        }

        if(order.getStatus() == EOrderStatus.CANCELLED || order.getStatus() == EOrderStatus.PICKED_UP) {
            throw new IllegalStateException("Cannot process payment for a cancelled or picked-up order.");
        }

        if(paymentRequest.getAmount().compareTo(order.getTotalPrice()) != 0) {
            throw new IllegalStateException("Payment amount does not match order total price.");
        }

        Payment newPayment = new Payment();
        newPayment.setUser(currentUser);
        newPayment.setOrder(order);
        newPayment.setAmount(paymentRequest.getAmount());
        newPayment.setPaymentMethod(paymentRequest.getPaymentMethod());
        newPayment.setStatus(EPaymentStatus.COMPLETED);

        Payment savedPayment = paymentRepository.save(newPayment);

        return mapToPaymentResponse(savedPayment);
       }
    
       /**
     * Get a specific payment by ID for the currently authenticated user (STUDENT).
     * Ensures user can only view their own payments.
     *
     * @param paymentId The ID of the payment to retrieve.
     * @return The PaymentResponse DTO.
     */
    @Transactional
    public PaymentResponse getMyPaymentById(Long paymentId) {
        Long currentUserId = getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found with id: " + currentUserId));

        Payment payment = paymentRepository.findByIdAndUser(paymentId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found or you don't have access to payment with id: " + paymentId));

        return mapToPaymentResponse(payment);
    }

    /**
     * Get all payments for the currently authenticated user (STUDENT).
     *
     * @return List of PaymentResponse DTOs.
     */
    @Transactional
    public List<PaymentResponse> getMyPayments() {
        Long currentUserId = getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found with id: " + currentUserId));

        List<Payment> payments = paymentRepository.findByUser(currentUser);
        return payments.stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get all payments (for CANTEEN_MANAGER/ADMIN).
     *
     * @return List of all PaymentResponse DTOs.
     */
    @Transactional
    public List<PaymentResponse> getAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream()
                .map(this::mapToPaymentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific payment by ID (for CANTEEN_MANAGER/ADMIN).
     *
     * @param paymentId The ID of the payment to retrieve.
     * @return The PaymentResponse DTO.
     */
    @Transactional
    public PaymentResponse getPaymentById(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));
        return mapToPaymentResponse(payment);
    }

    /**
     * Update the status of a payment (only by CANTEEN_MANAGER/ADMIN).
     *
     * @param paymentId The ID of the payment to update.
     * @param newStatus The new status for the payment.
     * @return The updated PaymentResponse DTO.
     */
    @Transactional
    public PaymentResponse updatePaymentStatus(Long paymentId, EPaymentStatus newStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        // Basic validation for status transitions (can be made more complex)
        if (payment.getStatus() == EPaymentStatus.REFUNDED || payment.getStatus() == EPaymentStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot update status of a refunded or cancelled payment.");
        }

        payment.setStatus(newStatus);
        Payment updatedPayment = paymentRepository.save(payment);
        return mapToPaymentResponse(updatedPayment);
    }
}