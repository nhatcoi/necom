package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.product.Product;
import com.example.ecommerceweb.entity.product.ProductRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface RatingRepository extends JpaRepository<ProductRating, Long> {

    @Query("SELECT pr.product FROM ProductRating pr GROUP BY pr.product ORDER BY AVG(pr.rating) DESC")
    List<Product> fetchTopRatedProducts(Pageable pageable);

    @Query("SELECT AVG(pr.rating) FROM ProductRating pr WHERE pr.product.id = :productId")
    BigDecimal avgRating(Long productId);


    @Query("SELECT r.product.id, AVG(r.rating) FROM ProductRating r WHERE r.product.id IN :productIds GROUP BY r.product.id")
    List<Object[]> findRawAverageRatings(List<Long> productIds);

    default Map<Long, Double> findAverageRatingsByProductIds(List<Long> productIds) {
        return findRawAverageRatings(productIds).stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Double) row[1]));
    }
}

