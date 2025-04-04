package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.CartItemAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemAttributeRepository extends JpaRepository<CartItemAttribute, Long> {
}
