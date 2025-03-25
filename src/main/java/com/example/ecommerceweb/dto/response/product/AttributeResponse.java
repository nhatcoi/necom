package com.example.ecommerceweb.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttributeResponse {
    private Long id;
    private String name;
    private String value;
}