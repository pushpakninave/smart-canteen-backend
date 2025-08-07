// E:\Salman(29-06-2025)\smart-canteen-backend\src\main\java\com\smartcanteen\service\OrderService.java

package com.smartcanteen.service;


import com.smartcanteen.exception.ResourceNotFoundException;
import com.smartcanteen.model.Fooditem;
import com.smartcanteen.model.Order;
import com.smartcanteen.model.OrderItem;
import com.smartcanteen.login.enity.EOrderStatus;
import com.smartcanteen.dto.OrderRequest;
import com.smartcanteen.dto.OrderItemRequest;
import com.smartcanteen.dto.OrderResponse;
import com.smartcanteen.dto.OrderItemResponse;
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
// Removed: import java.time.LocalDateTime; // No longer needed if no timestamps
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        FoodItemRepository foodItemRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
    }

    // Helper method to get the current authenticated user's ID
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

    // Helper method to map Order entity to OrderResponse DTO
    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getFoodItem().getId(),
                        item.getFoodItem().getName(),
                        item.getPriceAtOrder(),
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getUser().getUsername(),
                // Removed: order.getOrderDate(), // Field removed
                order.getTotalPrice(),
                order.getStatus(),
                itemResponses
        );
    }

    // --- Service Methods ---

    /**
     * Places a new order for the currently authenticated user.
     *
     * @param orderRequest The DTO containing the list of food items and quantities.
     * @return The created OrderResponse DTO.
     */
    @Transactional
    public OrderResponse placeOrder(OrderRequest orderRequest) {
        Long currentUserId = getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found with id: " + currentUserId));

        BigDecimal totalOrderPrice = BigDecimal.ZERO;
        Order newOrder = new Order();
        newOrder.setUser(currentUser);
        // Removed: newOrder.setOrderDate(LocalDateTime.now()); // No longer needed
        newOrder.setStatus(EOrderStatus.PENDING);

        if (orderRequest.getItems() == null || orderRequest.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            Fooditem foodItem = foodItemRepository.findById(itemRequest.getFoodItemId())
                    .orElseThrow(() -> new ResourceNotFoundException("Food item not found with id: " + itemRequest.getFoodItemId()));

            if (!foodItem.isAvailableToday()) {
                throw new IllegalArgumentException("Food item '" + foodItem.getName() + "' is currently not available.");
            }
            if (itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity for '" + foodItem.getName() + "' must be positive.");
            }

            BigDecimal itemSubtotal = foodItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setFoodItem(foodItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPriceAtOrder(foodItem.getPrice());
            orderItem.setSubtotal(itemSubtotal);

            newOrder.addOrderItem(orderItem);
            totalOrderPrice = totalOrderPrice.add(itemSubtotal);
        }

        newOrder.setTotalPrice(totalOrderPrice);

        Order savedOrder = orderRepository.save(newOrder);

        return mapToOrderResponse(savedOrder);
    }

    /**
     * Get all orders for the currently authenticated user (STUDENT).
     *
     * @return List of OrderResponse DTOs.
     */
    @Transactional
    public List<OrderResponse> getMyOrders() {
        Long currentUserId = getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found with id: " + currentUserId));

        List<Order> orders = orderRepository.findByUser(currentUser);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific order by ID for the currently authenticated user (STUDENT).
     * Ensures user can only view their own orders.
     *
     * @param orderId The ID of the order to retrieve.
     * @return The OrderResponse DTO.
     */
    @Transactional
    public OrderResponse getMyOrderById(Long orderId) {
        Long currentUserId = getCurrentUserId();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found with id: " + currentUserId));

        Order order = orderRepository.findByIdAndUser(orderId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found or you don't have access to order with id: " + orderId));

        return mapToOrderResponse(order);
    }

    /**
     * Get all orders (for CANTEEN_MANAGER).
     *
     * @return List of all OrderResponse DTOs.
     */
    @Transactional
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific order by ID (for CANTEEN_MANAGER).
     *
     * @param orderId The ID of the order to retrieve.
     * @return The OrderResponse DTO.
     */
    @Transactional
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
        return mapToOrderResponse(order);
    }

    /**
     * Update the status of an order (only by CANTEEN_MANAGER).
     *
     * @param orderId The ID of the order to update.
     * @param newStatus The new status for the order.
     * @return The updated OrderResponse DTO.
     */
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, EOrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (order.getStatus() == EOrderStatus.CANCELLED || order.getStatus() == EOrderStatus.PICKED_UP) {
            throw new IllegalArgumentException("Cannot update status of a cancelled or picked-up order.");
        }
        if (newStatus == EOrderStatus.CANCELLED && (order.getStatus() == EOrderStatus.PICKED_UP)) {
            throw new IllegalArgumentException("Cannot cancel an order that is already picked up.");
        }

        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return mapToOrderResponse(updatedOrder);
    }

    /**
     * Cancel an order (by STUDENT for their own order, or CANTEEN_MANAGER for any).
     *
     * @param orderId The ID of the order to cancel.
     * @param isManager True if the request is from a manager, false if from a student.
     */
    @Transactional
    public void cancelOrder(Long orderId, boolean isManager) {
        Long currentUserId = getCurrentUserId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!isManager && !order.getUser().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("You are not authorized to cancel this order.");
        }

        if (order.getStatus() == EOrderStatus.PICKED_UP) {
            throw new IllegalArgumentException("Cannot cancel an order that has already been picked up.");
        }
        if (order.getStatus() == EOrderStatus.CANCELLED) {
            return;
        }

        order.setStatus(EOrderStatus.CANCELLED);
        orderRepository.save(order);
    }
}