package com.necom.dto.address;

import lombok.Data;

@Data
public class AddressRequest {
    private String line;
    private Long provinceId;
    private Long districtId;
    private Long wardId;
}
