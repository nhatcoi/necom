package com.example.ecommerceweb.entity;

import com.example.ecommerceweb.entity.product.ProductAttribute;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_item_attribute")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CartItemAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_item_id", nullable = false)
    private CartItem cartItem;

    @ManyToOne
    @JoinColumn(name = "product_attribute_id", nullable = false)
    private ProductAttribute productAttribute;
}

