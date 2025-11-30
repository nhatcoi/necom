package com.necom.repository.employee;

import com.necom.entity.employee.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JobTypeRepository extends JpaRepository<JobType, Long>, JpaSpecificationExecutor<JobType> {
}