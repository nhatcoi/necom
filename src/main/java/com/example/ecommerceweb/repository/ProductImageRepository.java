package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductId(Long productId);

    @Query("SELECT pi.imageUrl FROM ProductImage pi WHERE pi.product.id = :productId")
    List<String> findUrlImageByProductId(@Param("productId") Long productId);
}
