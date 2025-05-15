package com.example.ecommerceweb.entity.product;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "specification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class  Specification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;
}
