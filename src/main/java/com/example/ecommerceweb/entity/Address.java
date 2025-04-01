package com.example.ecommerceweb.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    private User user;

    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "country")
    private String country;

    @Column(name = "is_main")
    private Boolean isMain;
}
