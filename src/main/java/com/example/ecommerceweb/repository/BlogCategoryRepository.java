package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.BlogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogCategoryRepository extends JpaRepository<BlogCategory, Long> {

    @Query(value = "SELECT bc.*, COUNT(b.id) AS blog_count " +
            "FROM blog_categories bc " +
            "LEFT JOIN blogs b ON b.blog_category_id = bc.id " +
            "GROUP BY bc.id " +
            "ORDER BY blog_count DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findTopBlogCategories();
}
