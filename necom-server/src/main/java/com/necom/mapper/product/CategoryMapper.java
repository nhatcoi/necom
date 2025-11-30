package com.necom.mapper.product;

import com.necom.dto.product.CategoryRequest;
import com.necom.dto.product.CategoryResponse;
import com.necom.entity.product.Category;
import com.necom.mapper.GenericMapper;
import com.necom.utils.MapperUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = MapperUtils.class)
public interface CategoryMapper extends GenericMapper<Category, CategoryRequest, CategoryResponse> {

    @Override
    @Mapping(source = "parentCategoryId", target = "parentCategory")
    Category requestToEntity(CategoryRequest request);

    @Override
    @Mapping(source = "parentCategoryId", target = "parentCategory")
    Category partialUpdate(@MappingTarget Category entity, CategoryRequest request);

}
