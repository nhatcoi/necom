package com.example.ecommerceweb.specification;

import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import com.example.ecommerceweb.entity.Order;
import com.example.ecommerceweb.dto.OrderFilter;

public class OrderSpecification {
    public static Specification<Order> getSpecification(OrderFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search condition
            if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {
                String likePattern = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("id")), likePattern),
                    cb.like(cb.lower(root.get("fullName")), likePattern)
                ));
            }

            // Status condition
            if (filter.getStatus() != null && !filter.getStatus().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            // Payment status condition
            if (filter.getPaymentStatus() != null && !filter.getPaymentStatus().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("paymentStatus"), filter.getPaymentStatus()));
            }

            // Date range conditions
            if (filter.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt").as(LocalDate.class), filter.getFromDate()));
            }

            if (filter.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt").as(LocalDate.class), filter.getToDate()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
