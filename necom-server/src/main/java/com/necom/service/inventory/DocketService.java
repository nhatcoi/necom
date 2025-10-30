package com.necom.service.inventory;

import com.necom.dto.inventory.DocketRequest;
import com.necom.dto.inventory.DocketResponse;
import com.necom.service.CrudService;

public interface DocketService extends CrudService<Long, DocketRequest, DocketResponse> {}
