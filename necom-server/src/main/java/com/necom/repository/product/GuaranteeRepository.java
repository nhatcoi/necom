package com.necom.repository.product;

import com.necom.entity.product.Guarantee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface GuaranteeRepository extends JpaRepository<Guarantee, Long>, JpaSpecificationExecutor<Guarantee> {
}