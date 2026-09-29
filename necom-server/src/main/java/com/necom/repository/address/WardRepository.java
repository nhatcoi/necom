package com.necom.repository.address;

import com.necom.entity.address.Ward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WardRepository extends JpaRepository<Ward, Long>, JpaSpecificationExecutor<Ward> {

    @Query("SELECT w FROM Ward w JOIN w.province p WHERE w.name = :wardName AND p.name = :provinceName")
    List<Ward> findByNameAndProvinceName(@Param("wardName") String wardName, @Param("provinceName") String provinceName);

}
