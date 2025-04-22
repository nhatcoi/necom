package com.example.ecommerceweb.entity;

import com.example.ecommerceweb.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tokens")
@Entity
@Builder
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 255)
    private String token;

    @Column(name = "expiration_date", nullable = false)
    private Instant expirationDate;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "expired", nullable = false)
    private boolean expired;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", length = 50, nullable = false)
    private TokenType tokenType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
