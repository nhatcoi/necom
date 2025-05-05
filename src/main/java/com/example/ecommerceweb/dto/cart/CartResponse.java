package com.example.ecommerceweb.dto.cart;

import com.example.ecommerceweb.dto.product.ProductDetailResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartResponse {
    private Long id;
    private ProductDetailResponse product;
    private Integer quantity;
    private BigDecimal price;
    private List<SelectedAttributeDTO> selectedAttributes;
    private BigDecimal totalPrice;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SelectedAttributeDTO {
        private Long id;
        private String attributeName;
        private String attributeValue;
        private BigDecimal price;
        private Integer quantity;
    }

}
