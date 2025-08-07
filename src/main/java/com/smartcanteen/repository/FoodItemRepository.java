package com.smartcanteen.repository;


import com.smartcanteen.model.Fooditem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface  FoodItemRepository  extends JpaRepository<Fooditem, Long> {

    // Custom method to check if a food item with a given name already exists
    boolean existsByName(String name);

    // Optional: Find by name (e.g., if you need to fetch it by name sometimes)
    Optional<Fooditem> findByName(String name);

    // Find all food items that are marked as available today
    List<Fooditem> findByAvailableTodayTrue();
    List<Fooditem> findByDonatedAtIsNotNullAndAvailableTodayFalse();

}
