package com.necom.mapper.employee;

import com.necom.dto.employee.JobTypeRequest;
import com.necom.dto.employee.JobTypeResponse;
import com.necom.entity.employee.JobType;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobTypeMapper extends GenericMapper<JobType, JobTypeRequest, JobTypeResponse> {
}
