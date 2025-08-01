# smart-canteen-backend

# 

1. FoodItem Module



\# 🍽️ FoodItem Module – Smart Canteen Management System



The `FoodItem` module is a core component of the \*\*Smart Canteen System\*\*. It manages all food-related operations including adding, listing, updating, deleting items, and tracking donation-related activities such as when food is marked as donated and when it is picked up by NGOs.



---



\## 📌 Module Responsibilities



\- Add new food items

\- Update existing food items

\- Delete food items

\- List all food items

\- Mark items as available for today’s menu

\- Handle donation of leftover food

\- Record NGO pickup after donation



---



\## 📁 Project Structure



fooditem/

│

├── controller/

│ └── FoodItemController.java # Defines REST API endpoints for food item operations

│

├── service/

│ └── FoodItemService.java # Contains business logic for food item management

│

├── dto/

│ ├── FoodItemRequest.java # Input DTO for creating or updating food items

│ └── FoodItemResponse.java # Output DTO sent in API responses

│

├── model/

│ └── FoodItem.java # Entity class mapped to the food\_items table

│

├── repository/

│ └── FoodItemRepository.java # JPA repository interface for DB operations

│

└── exception/

└── ResourceNotFoundException.java # Custom exception thrown when item not found





---



\## 🧠 How it Works – Flow Explanation



\### ✅ Add New Food Item

1\. Admin/Manager sends a POST request with food data.

2\. Controller receives and forwards to the service layer.

3\. Service converts DTO to entity and saves using repository.

4\. Repository stores data in the database.

5\. Response is returned using `FoodItemResponse` DTO.



---



\### ✅ Update Food Item

1\. A PUT request is sent to update food item details.

2\. Controller fetches the item and passes it to the service.

3\. Service updates and saves changes.

4\. Updated response is sent back.



---



\### ✅ Mark Available Today

1\. API: `PUT /api/food-items/{id}/available-today`

2\. Sets the `availableToday` flag to true.

3\. Used to dynamically show items on today's menu.



---



\### ✅ Handle Food Donation

1\. API: `PUT /api/food-items/{id}/donate`

&nbsp;  - Sets `donatedAt = currentTime`

&nbsp;  - Used when food is marked for donation.



2\. API: `PUT /api/food-items/{id}/received-by-ngo`

&nbsp;  - Sets `receivedByNgoAt = currentTime`

&nbsp;  - Marks that food has been picked up by an NGO.



---



\## 🗃️ `FoodItem` Entity Fields



```java

private Long id;

private String name;

private String description;

private BigDecimal price;

private boolean availableToday;

private LocalDateTime donatedAt;

private LocalDateTime receivedByNgoAt;



| Method | Endpoint                               | Description                   |

| ------ | -------------------------------------- | ----------------------------- |

| GET    | `/api/food-items`                      | List all food items           |

| POST   | `/api/food-items`                      | Add a new food item           |

| PUT    | `/api/food-items/{id}`                 | Update food item by ID        |

| DELETE | `/api/food-items/{id}`                 | Delete food item by ID        |

| PUT    | `/api/food-items/{id}/available-today` | Mark item as available today  |

| PUT    | `/api/food-items/{id}/donate`          | Mark item as donated          |

| PUT    | `/api/food-items/{id}/received-by-ngo` | Record NGO receiving the food |

















