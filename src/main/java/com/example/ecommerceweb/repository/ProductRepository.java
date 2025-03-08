package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    @Query("SELECT p FROM Product p JOIN FETCH p.categoryId")
    Page<Product> findAllProductsByPage(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.categoryId ORDER BY p.createdAt DESC")
    List<Product> fetchLatestProducts(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.categoryId WHERE p.price BETWEEN :min AND :max")
    List<Product> findByPriceBetween(double min, double max);

    @Query("SELECT p FROM Product p JOIN FETCH p.categoryId WHERE p.price BETWEEN :min AND :max")
    Page<Product> findByPriceBetween(double min, double max, Pageable pageable);

    @Query("SELECT p FROM Product p ORDER BY p.createdAt DESC")
    Page<Product> findAllProducts(Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.categoryId WHERE p.categoryId.id = :categoryId")
    Page<Product> findAllByCategoryId(Pageable pageable, Long categoryId);


    @Query(value = """
    SELECT * FROM products
    WHERE to_tsvector('english', name) @@ plainto_tsquery(:keyword)
       OR name ILIKE '%' || :keyword || '%'
    """, nativeQuery = true)
    Page<Product> findByNameContaining(Pageable pageable, @Param("keyword") String keyword);

}
