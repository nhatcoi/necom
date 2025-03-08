package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.response.PaginatedResponse;
import com.example.ecommerceweb.dto.ProductDTO;
import com.example.ecommerceweb.entity.Category;
import com.example.ecommerceweb.entity.Product;
import com.example.ecommerceweb.filter.ProductFilter;
import com.example.ecommerceweb.repository.CategoryRepository;
import com.example.ecommerceweb.repository.ProductRatingRepository;
import com.example.ecommerceweb.repository.ProductRepository;
import com.example.ecommerceweb.service.ProductService;
import com.example.ecommerceweb.specification.ProductSpecifications;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRatingRepository productRatingRepository;
    private final ModelMapper modelMapper;

    @Value("${image.base.url}")
    private String imageBaseurl;

    private static final Logger logger = Logger.getLogger(ProductServiceImpl.class.getName());

    @Override
    public Product createProduct(ProductDTO productDTO) throws IOException, InterruptedException {
        Optional<Category> cate = categoryRepository
                .findById(productDTO.getCategoryId());
        if (cate.isEmpty()) {
            return null;
        }
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .description(productDTO.getDescription())
                .categoryId(cate.get())
                .build();
        return productRepository.save(newProduct);
    }

    // get token if not access permission
//    public String getNameImage(String urlImage) throws MalformedURLException {
//        URL url = new URL(urlImage);
//        String fileName = url.getPath().substring(url.getPath().lastIndexOf('/') + 1);
//        if (fileName.contains("?")) {
//            fileName = fileName.substring(0, fileName.indexOf('?'));
//        }
//
//        return fileName;
//    }
//    public String getImageUrl(String fileName) {
//        String downloadToken = getDownloadToken("ecommerce-web-18f77.appspot.com", fileName);
//
//        return imageBaseurl
//                + URLEncoder.encode(fileName, StandardCharsets.UTF_8)
//                + "?alt=media&token=" + downloadToken;
//    }
//
//    public String getDownloadToken(String bucketName, String filePath) {
//        // Lấy Blob (file) từ Firebase Storage
//        Blob blob = StorageClient.getInstance().bucket(bucketName).get(filePath);
//
//        // Lấy metadata từ Blob và kiểm tra token
//        String downloadToken = Objects.requireNonNull(blob.getMetadata()).get("firebaseStorageDownloadTokens");
//
//        return downloadToken != null ? downloadToken : "";
//    }

    @Override
    public Product updateProduct(Long id, ProductDTO productDTO) {
        Product productToUpdate = getProductById(id);
        assert productToUpdate != null;
        // copy data from productDTO to productToUpdate
        // co the su dung ModelMapper
        productToUpdate = Product.builder()
                .id(id)
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .categoryId(categoryRepository.findById(productDTO.getCategoryId())
                        .orElse(null))
                .build();
        return productRepository.save(productToUpdate);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
    }

    @Override
    public Page<Product> getAllProducts(PageRequest pageRequest) {
        return productRepository.findAllProductsByPage(pageRequest);
    }

    @Override
    public boolean isProductExist(Long id) {
        return false;
    }

    @Override
    public List<ProductDTO> getLatestProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);

        List<Product>  productsLatest = productRepository.fetchLatestProducts(pageable);

        return productsLatest.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> getTopRatedProducts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Product>  productsLatest = productRepository.fetchLatestProducts(pageable);

        return productsLatest.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
    }

    @Override
    public List<ProductDTO> getProductByPriceRange(int minAmount, int maxAmount) {
        List<Product> products = productRepository.findByPriceBetween(minAmount, maxAmount);
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDTO.class))
                .toList();
    }

    @Override
    public Page<ProductDTO> getProductByPriceRange(int minAmount, int maxAmount, Pageable pageable) {
        return productRepository.findByPriceBetween(minAmount, maxAmount, pageable)
                .map(product -> modelMapper.map(product, ProductDTO.class));
    }


    @Override
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAllProducts(pageable)
                .map(product -> modelMapper.map(product, ProductDTO.class));
    }

    @Override
    public Page<ProductDTO> getProductsByCategory(Pageable pageable, Long categoryId) {
        return productRepository.findAllByCategoryId(pageable, categoryId)
                .map(product -> modelMapper.map(product, ProductDTO.class));
    }

    @Override
    public PaginatedResponse<ProductDTO> createPaginatedResponse(Page<ProductDTO> productDTOs) {
        return new PaginatedResponse<>(
                productDTOs.getContent(),
                productDTOs.getTotalPages(),
                productDTOs.getTotalElements(),
                productDTOs.getNumber(),
                productDTOs.getSize()
        );
    }

    @Override
    public Page<ProductDTO> searchProducts(Pageable pageable, String keyword) {
        Page<Product> products = productRepository.findByNameContaining(pageable, keyword);
        return products.map(product -> modelMapper.map(product, ProductDTO.class));
    }

    @Override
    public Page<ProductDTO> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> modelMapper.map(product, ProductDTO.class));
    }

    @Override
    public Page<ProductDTO> getFilteredProducts(ProductFilter filter, Pageable pageable) {
        Specification<Product> specQuery = ProductSpecifications.withFilters(filter);
        Page<Product> products = productRepository.findAll(specQuery, pageable);
        return products.map(product -> modelMapper.map(product, ProductDTO.class));
    }

//    @Override
//    public Page<ProductDTO> getNewProducts(Pageable pageable) {
//        Pageable sortedByCreatedAt = PageRequest.of(
//                pageable.getPageNumber(),
//                pageable.getPageSize(),
//                Sort.by(Sort.Direction.DESC, "createdAt")
//        );
//
//        Page<Product> newProducts = productRepository.findAll(sortedByCreatedAt);
//        return newProducts.map(product -> modelMapper.map(product, ProductDTO.class));
//    }
}
