package com.example.ecommerceweb.dto.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCartItemRequest {

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 5, message = "Quantity must be no more than 5")
    private Integer quantity;
}
