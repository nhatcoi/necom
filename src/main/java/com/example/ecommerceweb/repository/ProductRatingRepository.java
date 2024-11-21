package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entities.Product;
import com.example.ecommerceweb.entities.ProductRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ProductRatingRepository extends JpaRepository<ProductRating, Long> {

    @Query("SELECT pr.product FROM ProductRating pr GROUP BY pr.product ORDER BY AVG(pr.rating) DESC")
    List<Product> fetchTopRatedProducts(Pageable pageable);

    @Query("SELECT AVG(pr.rating) FROM ProductRating pr WHERE pr.product.id = :productId")
    Float avgRating(Long productId);
}

