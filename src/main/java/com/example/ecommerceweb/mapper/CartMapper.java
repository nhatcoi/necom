package com.example.ecommerceweb.mapper;

import com.example.ecommerceweb.dto.response.CartItemDTO;
import com.example.ecommerceweb.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {
    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "product.id", source = "productId")
    Cart toEntity(CartItemDTO dto);
}