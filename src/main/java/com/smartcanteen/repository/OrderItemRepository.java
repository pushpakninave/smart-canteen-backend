// OrderItem Repo
package com.smartcanteen.repository;

import com.smartcanteen.model.Fooditem;
import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    /**
     * Finds all OrderItem entities associated with a specific FoodItem.
     * This is used to delete child records before deleting the parent FoodItem
     * due to foreign key constraints.
     * @param foodItem The FoodItem entity to find associated OrderItems for.
     * @return A list of OrderItem entities.
     */
    List<OrderItem> findByFoodItem(Fooditem foodItem); // <<< NEW: This method was missing
}
