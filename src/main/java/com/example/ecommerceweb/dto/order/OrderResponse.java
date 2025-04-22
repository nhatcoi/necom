package com.example.ecommerceweb.dto.order;

import com.example.ecommerceweb.dto.product.ProductDTO;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private LocalDateTime orderDate;
    private String status;

    private List<ProductDTO> items;

    private BigDecimal total;
    private Boolean active;


}
