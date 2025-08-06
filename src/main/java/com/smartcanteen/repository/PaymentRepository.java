package com.smartcanteen.repository;

import com.smartcanteen.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.smartcanteen.security.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Custom method to find payments by user
    List<Payment> findByUser(User user);

    // Custom method to find a payment by ID and user(for security purposes)
    Optional<Payment> findByIdAndUser(Long id, User user);

    // Custom method to find payments by order ID(since it's OneToOne, it should be unique)
    Optional<Payment> findByOrderId(Long orderId);
}
