package com.necom.repository.inventory;

import com.necom.entity.inventory.CountVariant;
import com.necom.entity.inventory.CountVariantKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CountVariantRepository extends JpaRepository<CountVariant, CountVariantKey>,
        JpaSpecificationExecutor<CountVariant> {}
