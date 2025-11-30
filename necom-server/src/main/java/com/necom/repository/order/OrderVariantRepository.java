package com.necom.repository.order;

import com.necom.entity.order.OrderVariant;
import com.necom.entity.order.OrderVariantKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderVariantRepository extends JpaRepository<OrderVariant, OrderVariantKey>,
        JpaSpecificationExecutor<OrderVariant> {}
