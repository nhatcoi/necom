package com.necom.dto.address;

import lombok.Data;

import java.time.Instant;

@Data
public class DistrictResponse {
    private Long id;
    private Instant createdAt;
    private Instant updatedAt;
    private String name;
    private String code;
    private ProvinceResponse province;
}
