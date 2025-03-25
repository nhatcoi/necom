package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Long> {
}