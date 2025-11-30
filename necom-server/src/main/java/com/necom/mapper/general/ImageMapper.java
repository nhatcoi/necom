package com.necom.mapper.general;

import com.necom.dto.general.ImageRequest;
import com.necom.dto.general.ImageResponse;
import com.necom.entity.general.Image;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper extends GenericMapper<Image, ImageRequest, ImageResponse> {}
