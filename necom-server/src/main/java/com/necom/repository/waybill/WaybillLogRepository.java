package com.necom.repository.waybill;

import com.necom.entity.waybill.WaybillLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WaybillLogRepository extends JpaRepository<WaybillLog, Long>, JpaSpecificationExecutor<WaybillLog> {}
