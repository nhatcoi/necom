package com.necom.repository.inventory;

import com.necom.entity.inventory.DocketReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DocketReasonRepository extends JpaRepository<DocketReason, Long>, JpaSpecificationExecutor<DocketReason> {}
