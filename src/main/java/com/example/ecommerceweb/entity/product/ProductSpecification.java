package com.example.ecommerceweb.entity.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_specification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "specification_id", nullable = false)
    private Specification specification;

    @Column(name = "value", nullable = false)
    private String value;
}
