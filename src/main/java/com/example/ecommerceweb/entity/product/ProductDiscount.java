package com.example.ecommerceweb.entity.product;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "product_discount")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDiscount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "discount_id", nullable = false)
    private Discount discount;
}
