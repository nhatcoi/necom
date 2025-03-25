package com.example.ecommerceweb.entity.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@ToString(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "flash_sale_items")
@Entity
@Builder
public class FlashSaleItem  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flash_sale_id")
    @JsonBackReference
    private FlashSale flashSale;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product productSale;

    @Column(name = "sale_price", nullable = false)
    private Float salePrice;

    @Column(name = "quantity_limit", nullable = false)
    private Integer quantityLimit;
}