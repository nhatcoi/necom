package com.example.ecommerceweb.entity;

import com.example.ecommerceweb.entity.product.Product;
import com.example.ecommerceweb.entity.product.ProductAttribute;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_item")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "product_attribute_id")
    private ProductAttribute productAttribute;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price;

    @PrePersist
    @PreUpdate
    private void validateProduct() {
        if ((product == null && productAttribute == null) || (product != null && productAttribute != null)) {
            throw new IllegalArgumentException("Cart and/or product attributes are both null");
        }
    }
}