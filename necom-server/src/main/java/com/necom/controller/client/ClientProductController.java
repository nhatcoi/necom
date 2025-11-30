package com.necom.controller.client;

import com.necom.constant.AppConstants;
import com.necom.constant.FieldName;
import com.necom.constant.ResourceName;
import com.necom.dto.ListResponse;
import com.necom.dto.client.ClientListedProductResponse;
import com.necom.dto.client.ClientProductResponse;
import com.necom.entity.BaseEntity;
import com.necom.entity.product.Product;
import com.necom.exception.ResourceNotFoundException;
import com.necom.mapper.client.ClientProductMapper;
import com.necom.projection.inventory.SimpleProductInventory;
import com.necom.repository.ProjectionRepository;
import com.necom.repository.product.ProductRepository;
import com.necom.repository.review.ReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/client-api/products")
@AllArgsConstructor
@CrossOrigin(AppConstants.FRONTEND_HOST)
public class ClientProductController {

    private ProductRepository productRepository;
    private ProjectionRepository projectionRepository;
    private ClientProductMapper clientProductMapper;
    private ReviewRepository reviewRepository;

    @GetMapping
    public ResponseEntity<ListResponse<ClientListedProductResponse>> getAllProducts(
            @RequestParam(name = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(name = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "filter", required = false) @Nullable String filter,
            @RequestParam(name = "sort", required = false) @Nullable String sort,
            @RequestParam(name = "search", required = false) @Nullable String search,
            @RequestParam(name = "saleable", required = false) boolean saleable,
            @RequestParam(name = "newable", required = false) boolean newable
    ) {
        // Phân trang
        Pageable pageable = PageRequest.of(page - 1, size);

        // Lấy danh sách sản phẩm theo điều kiện lọc và phân trang
        Page<Product> products = productRepository.findByParams(filter, sort, search, saleable, newable, pageable);

        // Lấy thông tin tồn kho của sản phẩm
        List<Long> productIds = products.map(Product::getId).toList();
        List<SimpleProductInventory> productInventories = projectionRepository.findSimpleProductInventories(productIds);

        List<ClientListedProductResponse> clientListedProductResponses = products
                .map(product -> clientProductMapper.entityToListedResponse(product, productInventories)).toList();

        return ResponseEntity.status(HttpStatus.OK).body(ListResponse.of(clientListedProductResponses, products));
    }

    @GetMapping("/{slug}")
    public ResponseEntity<ClientProductResponse> getProduct(@PathVariable String slug) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException(ResourceName.PRODUCT, FieldName.SLUG, slug));

        List<SimpleProductInventory> productInventories = projectionRepository
                .findSimpleProductInventories(List.of(product.getId()));

        int averageRatingScore = reviewRepository.findAverageRatingScoreByProductId(product.getId());
        int countReviews = reviewRepository.countByProductId(product.getId());

        // Related Products
        Page<Product> relatedProducts = productRepository.findByParams(
                String.format("category.id==%s;id!=%s",
                        Optional.ofNullable(product.getCategory())
                                .map(BaseEntity::getId)
                                .map(Object::toString)
                                .orElse("0"),
                        product.getId()),
                "random",
                null,
                false,
                false,
                PageRequest.of(0, 4));

        List<Long> relatedProductIds = relatedProducts.map(Product::getId).toList();
        List<SimpleProductInventory> relatedProductInventories = projectionRepository
                .findSimpleProductInventories(relatedProductIds);

        List<ClientListedProductResponse> relatedProductResponses = relatedProducts
                .map(p -> clientProductMapper.entityToListedResponse(p, relatedProductInventories)).toList();

        // Result
        ClientProductResponse clientProductResponse = clientProductMapper
                .entityToResponse(product, productInventories, averageRatingScore, countReviews, relatedProductResponses);

        return ResponseEntity.status(HttpStatus.OK).body(clientProductResponse);
    }

}
