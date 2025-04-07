package com.example.ecommerceweb.specification;

import com.example.ecommerceweb.entity.Blog;
import org.springframework.data.jpa.domain.Specification;

public class BlogSpecification {
    public static Specification<Blog> hasCategory(String category) {
        return (root, query, cb) -> category == null ? null :
                cb.equal(root.get("blogCategory").get("name"), category);
    }

    public static Specification<Blog> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return null;
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), likePattern),
                    cb.like(cb.lower(root.get("content")), likePattern)
            );
        };
    }

    public static Specification<Blog> sortByCreatedAtDesc() {
        return (root, query, cb) -> {
            assert query != null;
            if (query.getResultType() != Long.class) {
                query.orderBy(cb.desc(root.get("createdAt")));
            }
            return null;
        };
    }

    public static Specification<Blog> sortByViews(String views) {
        return (root, query, cb) -> {
            if (query != null && query.getResultType() != Long.class && views != null) {
                if ("desc".equalsIgnoreCase(views)) {
                    query.orderBy(cb.desc(root.get("views")));
                } else if ("asc".equalsIgnoreCase(views)) {
                    query.orderBy(cb.asc(root.get("views")));
                }
            }
            return null;
        };
    }
}