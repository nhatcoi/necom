package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.dto.product.*;
import com.example.ecommerceweb.dto.response_data.PaginatedResponse;
import com.example.ecommerceweb.entity.Category;
import com.example.ecommerceweb.entity.product.*;
import com.example.ecommerceweb.enums.ProductStatus;
import com.example.ecommerceweb.exception.ErrorCode;
import com.example.ecommerceweb.exception.ResourceException;
import com.example.ecommerceweb.filter.ProductFilter;
import com.example.ecommerceweb.mapper.ProductMapper;
import com.example.ecommerceweb.repository.*;
import com.example.ecommerceweb.service.CategoryService;
import com.example.ecommerceweb.service.ProductService;
import com.example.ecommerceweb.specification.ProductSpecifications;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.example.ecommerceweb.util.Static.convertToSlug;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final SpecificationRepository specificationRepository;
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductImageService productImageService;
    private final RatingService ratingService;
    private final ModelMapper modelMapper;
    private final ProductMapper productMapper;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final FirebaseStorageService firebaseStorageService;

    @Value("${image.base.url}")
    private String imageBaseurl;


    @Override
    @Transactional
    public long createProduct(ProductCreateRequest request) {
        try {
            Category cate = categoryService.getCategoryById(request.getCategoryId());

            Product newProduct = Product.builder()
                    .name(request.getName())
                    .price(request.getPrice())
                    .originalPrice(request.getOriginalPrice())
                    .quantityInStock(request.getStock())
                    .status(request.getStock() > 0 ? ProductStatus.IN_STOCK : ProductStatus.OUT_OF_STOCK)
                    .slug(convertToSlug(request.getName()))
                    .categoryId(cate)
                    .description(request.getDescription())
                    .build();

            // Handle specifications
            List<SpecificationDto> specDtos = request.getSpecifications();
            if (specDtos != null && !specDtos.isEmpty()) {
                List<String> specNames = specDtos.stream()
                        .map(SpecificationDto::getName)
                        .distinct()
                        .toList();
                List<com.example.ecommerceweb.entity.product.Specification> existingSpecs
                        = specificationRepository.findByNameIn(specNames);

                Map<String, com.example.ecommerceweb.entity.product.Specification> specMap = existingSpecs.stream()
                        .collect(Collectors.toMap(com.example.ecommerceweb.entity.product.Specification::getName, Function.identity()));

                for (String name : specNames) {
                    if (!specMap.containsKey(name)) {
                        com.example.ecommerceweb.entity.product.Specification newSpec = new com.example.ecommerceweb.entity.product.Specification();
                        newSpec.setName(name);
                        specificationRepository.save(newSpec);
                        specMap.put(name, newSpec);
                    }
                }

                List<ProductSpecification> productSpecifications = new ArrayList<>();
                for (SpecificationDto dto : specDtos) {
                    com.example.ecommerceweb.entity.product.Specification spec = specMap.get(dto.getName());

                    ProductSpecification ps = new ProductSpecification();
                    ps.setProduct(newProduct);
                    ps.setSpecification(spec);
                    ps.setValue(dto.getValue());

                    productSpecifications.add(ps);
                }

                newProduct.setSpecifications(productSpecifications);
            }

            // Handle attributes
            List<AttributeDto> attributeDtos = request.getAttributes();
            if (attributeDtos != null && !attributeDtos.isEmpty()) {
                List<String> attributeNames = attributeDtos.stream()
                        .map(AttributeDto::getName)
                        .distinct()
                        .toList();

                List<Attribute> existingAttributes = attributeRepository.findByNameIn(attributeNames);
                Map<String, Attribute> attributeMap = existingAttributes.stream()
                        .collect(Collectors.toMap(Attribute::getName, Function.identity()));

                // Create new attributes if they don't exist
                for (String name : attributeNames) {
                    if (!attributeMap.containsKey(name)) {
                        Attribute newAttr = new Attribute();
                        newAttr.setName(name);
                        attributeRepository.save(newAttr);
                        attributeMap.put(name, newAttr);
                    }
                }

                List<ProductAttribute> productAttributes = new ArrayList<>();
                for (AttributeDto dto : attributeDtos) {
                    Attribute attribute = attributeMap.get(dto.getName());
                    
                    // Create AttributeValue
                    AttributeValue attrValue = new AttributeValue();
                    attrValue.setAttribute(attribute);
                    attrValue.setValue(dto.getValue());
                    attributeValueRepository.save(attrValue);

                    // Create ProductAttribute
                    ProductAttribute pa = new ProductAttribute();
                    pa.setProduct(newProduct);
                    pa.setAttributeValue(attrValue);
                    pa.setPrice(dto.getPrice());
                    pa.setStockQuantity(dto.getStockQuantity() != null ? dto.getStockQuantity() : 0);
                    
                    productAttributes.add(pa);
                }

                newProduct.setAttributes(productAttributes);
            }

            // Save the product first to get its ID
            Product savedProduct = productRepository.save(newProduct);

            // Handle thumbnail and product images after product is saved
            try {
                // Handle thumbnail
                if (request.getThumbnail() != null) {
                    String thumbnailUrl = firebaseStorageService.uploadImage(request.getThumbnail());
                    savedProduct.setThumbnail(thumbnailUrl);
                    savedProduct = productRepository.save(savedProduct);
                }

                // Handle product images
                if (request.getProductImages() != null && !request.getProductImages().isEmpty()) {
                    for (MultipartFile image : request.getProductImages()) {
                        String imageUrl = firebaseStorageService.uploadImage(image);
                        productImageService.saveProductImage(savedProduct, imageUrl);
                    }
                }
            } catch (Exception e) {
                log.error("Error uploading images for product {}: {}", savedProduct.getId(), e.getMessage());
                // Continue with the product creation even if image upload fails
            }

            return savedProduct.getId();
        } catch (Exception e) {
            log.error("Error creating product: {}", e.getMessage());
            throw new ResourceException(ErrorCode.RESOURCE_NOT_FOUND, "Error creating product: " + e.getMessage());
        }
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
        Product productToUpdate = productRepository.findById(id).orElse(null);
        assert productToUpdate != null;
        // copy data from productDTO to productToUpdate
        // co the su dung ModelMapper
        productToUpdate = Product.builder()
                .id(id)
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .thumbnail(productDTO.getThumbnail())
                .categoryId(categoryService.getCategoryById(productDTO.getCategoryId()))
                .build();
        return productRepository.save(productToUpdate);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public ProductDetailResponse getProductById(Long idProduct) {
        Product product = productRepository.findById(idProduct)
                .orElseThrow(() -> new ResourceException(ErrorCode.RESOURCE_NOT_FOUND));
        ProductDetailResponse productDetailResponse = productMapper.productToProductDetailResponse(product);
        productDetailResponse.setAvgRating(ratingService.avgRating(idProduct));
        productDetailResponse.setProductImages(productImageService.getImageListUrl(idProduct));

        return productDetailResponse;
    }

    @Override
    public ProductDetailResponse getProductBySlug(String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceException(ErrorCode.RESOURCE_NOT_FOUND));
        ProductDetailResponse productDetailResponse = productMapper.productToProductDetailResponse(product);
        productDetailResponse.setAvgRating(ratingService.avgRating(product.getId()));
        productDetailResponse.setProductImages(productImageService.getImageListUrl(product.getId()));

        return productDetailResponse;
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
