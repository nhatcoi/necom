package com.example.ecommerceweb.service;


import com.example.ecommerceweb.dto.product.ProductCreateRequest;
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

    long createProduct(ProductCreateRequest request) throws IOException, InterruptedException;

    void deleteProduct(Long id);

    ProductDetailResponse getProductById(Long id);

    ProductDetailResponse getProductBySlug(String slug);

    Page<ProductDTO> getProducts(Pageable pageable);

    Page<ProductDTO> getFilteredProducts(ProductFilter filter, Pageable pageable);

    Product updateProduct(Long id, ProductDTO productDTO);
}
