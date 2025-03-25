package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.product.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    @Query("SELECT fs FROM FlashSale fs JOIN FETCH fs.items WHERE fs.startTime < :currentStartTime AND fs.endTime > :currentEndTime")
    List<FlashSale> findAllByStartTimeBeforeAndEndTimeAfter(
            @Param("currentStartTime") LocalDateTime currentStartTime,
            @Param("currentEndTime") LocalDateTime currentEndTime);

    List<FlashSale> findAllByEndTimeBefore(LocalDateTime currentTime);
}
