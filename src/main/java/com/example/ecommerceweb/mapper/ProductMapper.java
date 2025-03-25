package com.example.ecommerceweb.mapper;
import com.example.ecommerceweb.dto.response.product.ProductAttributeDTO;
import com.example.ecommerceweb.dto.response.product.ProductDetailResponse;
import com.example.ecommerceweb.dto.response.product.ProductSpecificationDTO;
import com.example.ecommerceweb.entity.product.Product;
import com.example.ecommerceweb.entity.product.ProductAttribute;
import com.example.ecommerceweb.entity.product.ProductSpecification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "categoryId.id", target = "categoryId")
    ProductDetailResponse productToProductDetailResponse(Product product);


    @Mapping(source = "specification.name", target = "name")
    ProductSpecificationDTO productSpecificationToDTO(ProductSpecification specification);

    @Mapping(source = "attributeValue.attribute.name", target = "attributeName")
    @Mapping(source = "attributeValue.value", target = "attributeValue")
    ProductAttributeDTO productAttributeToDTO(ProductAttribute attribute);

    List<ProductSpecificationDTO> productSpecificationsToDTOs(List<ProductSpecification> specifications);
    List<ProductAttributeDTO> productAttributesToDTOs(List<ProductAttribute> attributes);
}
