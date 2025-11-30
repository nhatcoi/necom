package com.necom.repository.inventory;

import com.necom.entity.inventory.PurchaseOrderVariant;
import com.necom.entity.inventory.PurchaseOrderVariantKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PurchaseOrderVariantRepository extends JpaRepository<PurchaseOrderVariant, PurchaseOrderVariantKey>,
        JpaSpecificationExecutor<PurchaseOrderVariant> {}
