package com.smartcanteen.controller;

import com.smartcanteen.dto.FoodItemRequest;
import com.smartcanteen.dto.FoodItemResponse;
import com.smartcanteen.service.FoodService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


// handles all fooditems
@RestController
@RequestMapping("/api/food")
public class FoodController {

    private final FoodService foodService;

    public FoodController(FoodService foodService) {
        this.foodService = foodService;
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ROLE_CANTEEN_MANAGER', 'ROLE_ADMIN')") // Use ROLE_ prefix
    public ResponseEntity<List<FoodItemResponse>> getAllFoodItems() {
        List<FoodItemResponse> foodItems = foodService.getAllFoodItems();
        return ResponseEntity.ok(foodItems);
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_CANTEEN_MANAGER', 'ROLE_ADMIN')") // Use ROLE_ prefix
    public ResponseEntity<List<FoodItemResponse>> getAvailableFoodItems() {
        List<FoodItemResponse> foodItems = foodService.getAvailableFoodItems();
        return ResponseEntity.ok(foodItems);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_CANTEEN_MANAGER', 'ROLE_ADMIN', 'ROLE_NGO')") // Use ROLE_ prefix
    public ResponseEntity<FoodItemResponse> getFoodItemById(@PathVariable Long id) {
        FoodItemResponse foodItem = foodService.getFoodItemById(id);
        return ResponseEntity.ok(foodItem);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ROLE_CANTEEN_MANAGER')") // Use ROLE_ prefix
    public ResponseEntity<FoodItemResponse> createFoodItem(@Valid @RequestBody FoodItemRequest foodItemRequest) {
        FoodItemResponse newFoodItem = foodService.createFoodItem(foodItemRequest);
        return new ResponseEntity<>(newFoodItem, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CANTEEN_MANAGER')") // Use ROLE_ prefix
    public ResponseEntity<FoodItemResponse> updateFoodItem(@PathVariable Long id, @Valid @RequestBody FoodItemRequest foodItemRequest) {
        FoodItemResponse updatedFoodItem = foodService.updateFoodItem(id, foodItemRequest);
        return ResponseEntity.ok(updatedFoodItem);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CANTEEN_MANAGER')") // Use ROLE_ prefix
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Long id) {
        foodService.deleteFoodItem(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/toggle-availability")
    @PreAuthorize("hasRole('ROLE_CANTEEN_MANAGER')") // Use ROLE_ prefix
    public ResponseEntity<FoodItemResponse> toggleFoodAvailability(@PathVariable Long id, @RequestBody boolean isAvailable) {
        FoodItemResponse updatedFoodItem = foodService.toggleFoodAvailability(id, isAvailable);
        return ResponseEntity.ok(updatedFoodItem);
    }

    @PutMapping("/{id}/donate")
    @PreAuthorize("hasRole('ROLE_CANTEEN_MANAGER')") // Use ROLE_ prefix
    public ResponseEntity<FoodItemResponse> donateFoodItem(@PathVariable Long id) {
        FoodItemResponse donatedFoodItem = foodService.donateFoodItem(id);
        return ResponseEntity.ok(donatedFoodItem);
    }

    @GetMapping("/donated")
    @PreAuthorize("hasRole('ROLE_NGO')") // Use ROLE_NGO as per updated ERole
    public ResponseEntity<List<FoodItemResponse>> getDonatedFoodItems() {
        List<FoodItemResponse> donatedItems = foodService.getDonatedFoodItems();
        return ResponseEntity.ok(donatedItems);
    }

    @PutMapping("/{id}/mark-received")
    @PreAuthorize("hasRole('ROLE_NGO')") // Only NGO role can access this
    public ResponseEntity<FoodItemResponse> markDonatedItemAsReceived(@PathVariable Long id) {
        FoodItemResponse receivedItem = foodService.markDonatedItemAsReceived(id);
        return ResponseEntity.ok(receivedItem);
    }
}