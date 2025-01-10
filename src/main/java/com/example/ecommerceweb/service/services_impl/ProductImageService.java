package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.entity.Product;
import com.example.ecommerceweb.entity.ProductImage;
import com.example.ecommerceweb.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductImageService {
    private final ProductImageRepository productImageRepository;

    public void saveProductImage(Product product, String imageUrl) {
        ProductImage newProductImage = ProductImage.builder()
                .product(product)
                .imageUrl(imageUrl)
                .build();
        productImageRepository.save(newProductImage);
    }

    public List<ProductImage> getProductImagesByProductId(Long productId) {
        return productImageRepository.findByProductId(productId);
    }

    public List<String> getImageListUrl(Long productId) {
        return productImageRepository.findUrlImageByProductId(productId);
    }
}