package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entities.Blog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query("SELECT b FROM Blog b ORDER BY b.createdAt DESC")
    List<Blog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT b FROM Blog b WHERE b.categoryId.id = :categoryId  ORDER BY b.createdAt DESC")
    Page<Blog> findAllByCategoryId(Pageable pageable, Long categoryId);

    @Query("SELECT b FROM Blog b ORDER BY b.createdAt DESC")
    Page<Blog> findAllBlogs(Pageable pageable);
}
