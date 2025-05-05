package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RatingService {

    private final RatingRepository ratingRepository;

    public BigDecimal avgRating(Long productId) {
        return ratingRepository.avgRating(productId);
    }

    public Map<Long, Double> getAverageRatings(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            throw new IllegalArgumentException("Product IDs cannot be empty");
        }

        return ratingRepository.findAverageRatingsByProductIds(productIds);
    }
}
