// OrderItem Request DTO
package com.smartcanteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemRequest {
    private Long foodItemId;
    private Integer quantity;
}
