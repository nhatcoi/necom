package com.example.ecommerceweb.specification;

import com.example.ecommerceweb.entity.Product;
import com.example.ecommerceweb.filter.ProductFilter;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {
    public static Specification<Product> withFilters(ProductFilter filter) {
        return ((root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();

                if (filter.getSortByNew() != null && filter.getSortByNew()) {
                    assert query != null;
                    query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
                }

                if (filter.getCategory() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("categoryId").get("id"), filter.getCategory()));
                }

                if (filter.getMinPrice() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
                }

                if (filter.getMaxPrice() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
                }

                if (filter.getInStock() != null) {
                    predicates.add(criteriaBuilder.equal(root.get("quantityInStock"), filter.getInStock()));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
