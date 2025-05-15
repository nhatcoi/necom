package com.example.ecommerceweb.repository;



import com.example.ecommerceweb.entity.product.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecificationRepository extends JpaRepository<Specification, Long> {
    List<Specification> findByNameIn(List<String> specNames);
}
