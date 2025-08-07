package com.smartcanteen.repository;

import com.smartcanteen.model.Order;
import com.smartcanteen.security.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Find orders by the user who placed them
    List<Order> findByUser(User user);

    // Find a specific order by ID and user (for security/ownership checks)
    Optional<Order> findByIdAndUser(Long id, User user);

    // You might add more custom queries later, e.g., find by status, date range, etc.
}
