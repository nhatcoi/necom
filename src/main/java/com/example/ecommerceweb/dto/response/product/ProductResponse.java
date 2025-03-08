package com.example.ecommerceweb.dto.response.product;

import com.example.ecommerceweb.dto.ProductDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse extends ProductDTO {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<String> productImages;
    private Float avgRating;
}
