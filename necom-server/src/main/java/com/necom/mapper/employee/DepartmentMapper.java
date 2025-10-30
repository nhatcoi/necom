package com.necom.mapper.employee;

import com.necom.dto.employee.DepartmentRequest;
import com.necom.dto.employee.DepartmentResponse;
import com.necom.entity.employee.Department;
import com.necom.mapper.GenericMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentMapper extends GenericMapper<Department, DepartmentRequest, DepartmentResponse> {
}
