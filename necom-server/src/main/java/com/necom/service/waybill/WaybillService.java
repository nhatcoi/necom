package com.necom.service.waybill;

import com.necom.dto.waybill.GhnCallbackOrderRequest;
import com.necom.dto.waybill.WaybillRequest;
import com.necom.dto.waybill.WaybillResponse;
import com.necom.service.CrudService;

public interface WaybillService extends CrudService<Long, WaybillRequest, WaybillResponse> {

    void callbackStatusWaybillFromGHN(GhnCallbackOrderRequest ghnCallbackOrderRequest);

}
