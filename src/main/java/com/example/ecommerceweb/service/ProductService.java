package com.example.ecommerceweb.service;


import com.example.ecommerceweb.dto.response.PaginatedResponse;
import com.example.ecommerceweb.dto.ProductDTO;
import com.example.ecommerceweb.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public interface ProductService {
    Product createProduct(ProductDTO productDTO) throws IOException, InterruptedException;
    Product updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    Product getProductById(Long id);
    List<Product> getAllProducts();
    Page<Product> getAllProducts(PageRequest pageRequest);
    boolean isProductExist(Long id);

    List<ProductDTO> getLatestProducts(int limit);
    List<ProductDTO> getTopRatedProducts(int limit);
    List<Product> getProductByPriceRange(int min, int max);


    Page<ProductDTO> getAllProducts(Pageable pageable);
    Page<ProductDTO> getProductsByCategory(Pageable pageable, Long categoryId);
    PaginatedResponse<ProductDTO> createPaginatedResponse(Page<ProductDTO> productDTOs);

    List<ProductDTO> searchProducts(String keyword);
}
