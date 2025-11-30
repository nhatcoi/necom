package com.necom.mapper.product;

import com.necom.dto.product.TagRequest;
import com.necom.dto.product.TagResponse;
import com.necom.entity.product.Tag;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper extends GenericMapper<Tag, TagRequest, TagResponse> {
}
