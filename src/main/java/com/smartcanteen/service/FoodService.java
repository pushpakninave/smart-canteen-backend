// Food Service
package com.smartcanteen.service;


import com.smartcanteen.exception.ResourceNotFoundException;
import com.smartcanteen.model.Fooditem;
import com.smartcanteen.model.OrderItem;
import com.smartcanteen.dto.FoodItemRequest;
import com.smartcanteen.dto.FoodItemResponse;
import com.smartcanteen.repository.FoodItemRepository;
import com.smartcanteen.repository.OrderItemRepository;
import com.smartcanteen.repository.OrderRepository;
import com.smartcanteen.repository.UserRepository;
import com.smartcanteen.security.User;
import com.smartcanteen.security.UserDetailsImpl;

import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FoodService {

    private final FoodItemRepository foodItemRepository;
    private final OrderItemRepository orderItemRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public FoodService(FoodItemRepository foodItemRepository,
                       OrderItemRepository orderItemRepository,
                       UserRepository userRepository,
                       OrderRepository orderRepository) {
        this.foodItemRepository = foodItemRepository;
        this.orderItemRepository = orderItemRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User is not authenticated.");
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

    private FoodItemResponse mapToFoodItemResponse(Fooditem foodItem) {
        if (foodItem == null) {
            return null;
        }
        return new FoodItemResponse(
                foodItem.getId(),
                foodItem.getName(),
                foodItem.getDescription(),
                foodItem.getPrice(),
                foodItem.isAvailableToday(),
                foodItem.getDonatedAt(),
                foodItem.getReceivedByNgoAt()
        );
    }

    public List<FoodItemResponse> getAllFoodItems() {
        return foodItemRepository.findAll().stream()
                .map(this::mapToFoodItemResponse)
                .collect(Collectors.toList());
    }

    public List<FoodItemResponse> getAvailableFoodItems() {
        return foodItemRepository.findByAvailableTodayTrue().stream()
                .map(this::mapToFoodItemResponse)
                .collect(Collectors.toList());
    }

    public FoodItemResponse getFoodItemById(Long id) {
        Fooditem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem not found with id: " + id));
        return mapToFoodItemResponse(foodItem);
    }

    @Transactional
    public FoodItemResponse createFoodItem(FoodItemRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Food item name cannot be empty.");
        }
        if (foodItemRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Food item with name '" + request.getName() + "' already exists.");
        }

        Fooditem foodItem = new Fooditem();
        foodItem.setName(request.getName());
        foodItem.setDescription(request.getDescription());
        foodItem.setPrice(request.getPrice());
        if (request.getAvailable() != null) {
            foodItem.setAvailableToday(request.getAvailable());
        }
        foodItem.setDonatedAt(null);
        foodItem.setReceivedByNgoAt(null);

        Fooditem savedFoodItem = foodItemRepository.save(foodItem);
        return mapToFoodItemResponse(savedFoodItem);
    }

    @Transactional
    public FoodItemResponse updateFoodItem(Long id, FoodItemRequest request) {
        Fooditem existingFoodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem not found with id: " + id));

        if (request.getName() != null && !request.getName().trim().isEmpty() && !request.getName().equals(existingFoodItem.getName())) {
            if (foodItemRepository.existsByName(request.getName())) {
                throw new IllegalArgumentException("Food item with name '" + request.getName() + "' already exists.");
            }
            existingFoodItem.setName(request.getName());
        }

        if (request.getDescription() != null) {
            existingFoodItem.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            existingFoodItem.setPrice(request.getPrice());
        }
        if (request.getAvailable() != null) {
            existingFoodItem.setAvailableToday(request.getAvailable());
        }
        Fooditem updatedFoodItem = foodItemRepository.save(existingFoodItem);
        return mapToFoodItemResponse(updatedFoodItem);
    }

    @Transactional
    public void deleteFoodItem(Long id) {
        Fooditem foodItemToDelete = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem not found with id: " + id));

        List<OrderItem> associatedOrderItems = orderItemRepository.findByFoodItem(foodItemToDelete);
        orderItemRepository.deleteAll(associatedOrderItems);

        foodItemRepository.delete(foodItemToDelete);
    }

    @Transactional
    public FoodItemResponse toggleFoodAvailability(Long id, boolean availableToday) {
        Fooditem existingFoodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem not found with id: " + id));
        existingFoodItem.setAvailableToday(availableToday);
        if (availableToday) {
            existingFoodItem.setDonatedAt(null);
            existingFoodItem.setReceivedByNgoAt(null);
        }
        Fooditem updatedFoodItem = foodItemRepository.save(existingFoodItem);
        return mapToFoodItemResponse(updatedFoodItem);
    }

    @Transactional
    public FoodItemResponse donateFoodItem(Long id) {
        Fooditem foodItemToDonate = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem not found with id: " + id));

        if (foodItemToDonate.getDonatedAt() != null) {
            throw new IllegalArgumentException("Food item with id " + id + " has already been donated.");
        }

        foodItemToDonate.setAvailableToday(false);
        foodItemToDonate.setDonatedAt(LocalDateTime.now());
        foodItemToDonate.setReceivedByNgoAt(null);

        Fooditem updatedFoodItem = foodItemRepository.save(foodItemToDonate);
        return mapToFoodItemResponse(updatedFoodItem);
    }

    public List<FoodItemResponse> getDonatedFoodItems() {
        return foodItemRepository.findByDonatedAtIsNotNullAndAvailableTodayFalse().stream()
                .map(this::mapToFoodItemResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public FoodItemResponse markDonatedItemAsReceived(Long id) {
        Fooditem foodItem = foodItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("FoodItem not found with id: " + id));

        if (foodItem.getDonatedAt() == null) {
            throw new IllegalArgumentException("Food item with id " + id + " has not been marked as donated.");
        }
        if (foodItem.getReceivedByNgoAt() != null) {
            throw new IllegalArgumentException("Food item with id " + id + " has already been marked as received by NGO.");
        }

        foodItem.setReceivedByNgoAt(LocalDateTime.now());
        Fooditem updatedFoodItem = foodItemRepository.save(foodItem);
        return mapToFoodItemResponse(updatedFoodItem);
    }
}