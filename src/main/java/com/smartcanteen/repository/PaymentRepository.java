// E:\Salman(29-06-2025)\smart-canteen-backend\src\main\java\com\smartcanteen\repository\PaymentRepository.java

package com.smartcanteen.repository;

import com.smartcanteen.model.Payment;
import com.smartcanteen.security.User; // Import your User entity
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Custom method to find payments by user
    List<Payment> findByUser(User user);

    // Custom method to find a specific payment by ID and user (for security)
    Optional<Payment> findByIdAndUser(Long id, User user);

    // Custom method to find a payment by order (since it's OneToOne, it should be unique)
    Optional<Payment> findByOrderId(Long orderId);
}