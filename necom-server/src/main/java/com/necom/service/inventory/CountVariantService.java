package com.necom.service.inventory;

import com.necom.dto.inventory.CountVariantRequest;
import com.necom.dto.inventory.CountVariantResponse;
import com.necom.entity.inventory.CountVariantKey;
import com.necom.service.CrudService;

public interface CountVariantService extends CrudService<CountVariantKey, CountVariantRequest, CountVariantResponse> {}
