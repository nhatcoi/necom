package com.example.ecommerceweb.specification;

import com.example.ecommerceweb.entity.product.Product;
import com.example.ecommerceweb.filter.ProductFilter;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecifications {

    public static Specification<Product> withFilters(ProductFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getCategory() != null) {
                predicates.add(cb.equal(root.get("categoryId").get("id"), filter.getCategory()));
            }

            if (filter.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), filter.getMinPrice()));
            }

            if (filter.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), filter.getMaxPrice()));
            }

            if (filter.getInStock() != null) {
                if (filter.getInStock()) {
                    predicates.add(cb.greaterThan(root.get("quantityInStock"), 0));
                } else {
                    predicates.add(cb.equal(root.get("quantityInStock"), 0));
                }
            }
            

            if (filter.getSearch() != null && !filter.getSearch().isEmpty()) {
                String pattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), pattern));
            }

            assert query != null;
            if (query.getResultType() != Long.class && filter.getSortByNew() != null && filter.getSortByNew()) {
                query.orderBy(cb.desc(root.get("createdAt")));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Sort resolveSort(String sortBy) {
        if (sortBy == null) return Sort.unsorted();

        return switch (sortBy) {
            case "name_asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name_desc" -> Sort.by(Sort.Direction.DESC, "name");
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "stock_asc" -> Sort.by(Sort.Direction.ASC, "quantityInStock");
            case "stock_desc" -> Sort.by(Sort.Direction.DESC, "quantityInStock");
            case "created_desc" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "created_asc" -> Sort.by(Sort.Direction.ASC, "createdAt");
            default -> Sort.unsorted();
        };
    }
}