package com.example.ecommerceweb.entity.product;

import com.example.ecommerceweb.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_ratings")
@Entity
@Builder
public class ProductRating extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "rating")
    private Integer rating;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_id")
    private Long userId;
}
