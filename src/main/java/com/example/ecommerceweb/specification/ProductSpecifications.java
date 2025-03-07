package com.example.ecommerceweb.specification;

import com.example.ecommerceweb.dto.ProductDTO;
import com.example.ecommerceweb.filter.ProductFilter;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {
    public static Specification<ProductDTO> withFilters(ProductFilter filter) {
        return ((root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (filter.getCategory() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("category"), filter.getCategory()));
                }

                if (filter.getMinPrice() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
                }

                if (filter.getMaxPrice() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
