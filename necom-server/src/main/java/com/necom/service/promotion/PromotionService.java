package com.necom.service.promotion;

import com.necom.dto.promotion.PromotionRequest;
import com.necom.dto.promotion.PromotionResponse;
import com.necom.service.CrudService;

import java.time.Instant;

public interface PromotionService extends CrudService<Long, PromotionRequest, PromotionResponse> {

    boolean checkCanCreatePromotionForProduct(Long productId, Instant startDate, Instant endDate);

}
