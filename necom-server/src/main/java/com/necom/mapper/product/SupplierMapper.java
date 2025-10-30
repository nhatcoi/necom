package com.necom.mapper.product;

import com.necom.dto.product.SupplierRequest;
import com.necom.dto.product.SupplierResponse;
import com.necom.entity.product.Supplier;
import com.necom.mapper.GenericMapper;
import com.necom.mapper.address.AddressMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = AddressMapper.class)
public interface SupplierMapper extends GenericMapper<Supplier, SupplierRequest, SupplierResponse> {
}
