package com.example.ecommerceweb.dto.cart;

import com.example.ecommerceweb.dto.product.ProductDetailResponse;
import lombok.*;

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
    private Float price;
    private List<SelectedAttributeDTO> selectedAttributes;
    private Float totalPrice;

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SelectedAttributeDTO {
        private Long id;
        private String attributeName;
        private String attributeValue;
        private Float price;
        private Integer quantity;
    }

}
