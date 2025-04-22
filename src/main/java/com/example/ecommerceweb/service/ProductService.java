package com.example.ecommerceweb.service;


import com.example.ecommerceweb.dto.response_data.PaginatedResponse;
import com.example.ecommerceweb.dto.product.ProductDTO;
import com.example.ecommerceweb.dto.product.ProductDetailResponse;
import com.example.ecommerceweb.entity.product.Product;
import com.example.ecommerceweb.filter.ProductFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductService {
    Product createProduct(ProductDTO productDTO) throws IOException, InterruptedException;
    Product updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    ProductDetailResponse getProductById(Long id);

    ProductDetailResponse getProductBySlug(String slug);

    List<ProductDTO> getLatestProducts(int limit);
    List<ProductDTO> getTopRatedProducts(int limit);
    List<ProductDTO> getProductByPriceRange(int min, int max);
    Page<ProductDTO> getProductByPriceRange(int min, int max, Pageable pageable);


    Page<ProductDTO> getAllProducts(Pageable pageable);
    Page<ProductDTO> getProductsByCategory(Pageable pageable, Long categoryId);
    PaginatedResponse<ProductDTO> createPaginatedResponse(Page<ProductDTO> productDTOs);

    Page<ProductDTO> searchProducts(Pageable pageable, String keyword);
    Page<ProductDTO> getProducts(Pageable pageable);
    Page<ProductDTO> getFilteredProducts(ProductFilter filter, Pageable pageable);
}
