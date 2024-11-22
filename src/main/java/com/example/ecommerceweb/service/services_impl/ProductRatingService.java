package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.repository.ProductRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductRatingService {
    private final ProductRatingRepository productRatingRepository;

    public Float avgRating(Long productId) {
        return productRatingRepository.avgRating(productId);
    }
}
