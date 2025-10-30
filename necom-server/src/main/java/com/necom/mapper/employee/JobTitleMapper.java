package com.necom.mapper.employee;

import com.necom.dto.employee.JobTitleRequest;
import com.necom.dto.employee.JobTitleResponse;
import com.necom.entity.employee.JobTitle;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobTitleMapper extends GenericMapper<JobTitle, JobTitleRequest, JobTitleResponse> {
}
