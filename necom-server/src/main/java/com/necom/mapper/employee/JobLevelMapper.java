package com.necom.mapper.employee;

import com.necom.dto.employee.JobLevelRequest;
import com.necom.dto.employee.JobLevelResponse;
import com.necom.entity.employee.JobLevel;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobLevelMapper extends GenericMapper<JobLevel, JobLevelRequest, JobLevelResponse> {
}
