package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.product.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {
}
