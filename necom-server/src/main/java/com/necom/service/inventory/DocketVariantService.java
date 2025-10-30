package com.necom.service.inventory;

import com.necom.dto.inventory.DocketVariantRequest;
import com.necom.dto.inventory.DocketVariantResponse;
import com.necom.entity.inventory.DocketVariantKey;
import com.necom.service.CrudService;

public interface DocketVariantService extends CrudService<DocketVariantKey, DocketVariantRequest, DocketVariantResponse> {}
