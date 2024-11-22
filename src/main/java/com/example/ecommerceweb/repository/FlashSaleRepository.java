package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface FlashSaleRepository extends JpaRepository<FlashSale, Long> {

    List<FlashSale> findAllByStartTimeBeforeAndEndTimeAfter(LocalDateTime currentStartTime, LocalDateTime currentEndTime);

    List<FlashSale> findAllByEndTimeBefore(LocalDateTime currentTime);
}
