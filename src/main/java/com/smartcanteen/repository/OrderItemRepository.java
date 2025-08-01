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
	
	/*Purpose:
		This is used when you want to delete a FoodItem, but it still has related OrderItem records.

		Foreign Key Constraint:
		If you try deleting a Fooditem without cleaning up child records (OrderItem), the DB will throw an error.
	*/
    List<OrderItem> findByFoodItem(Fooditem foodItem); // <<< NEW: This method was missing
}

