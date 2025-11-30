package com.necom.dto.employee;

import com.necom.dto.address.AddressRequest;
import lombok.Data;

@Data
public class OfficeRequest {
    private String name;
    private AddressRequest address;
    private Integer status;
}
