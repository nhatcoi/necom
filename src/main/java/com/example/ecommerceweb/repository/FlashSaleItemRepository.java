package com.example.ecommerceweb.repository;

import com.example.ecommerceweb.entity.product.FlashSaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FlashSaleItemRepository extends JpaRepository<FlashSaleItem, Long> {

    List<FlashSaleItem> findByFlashSaleId(Long flashSaleId);

    FlashSaleItem findByFlashSaleIdAndProductSaleId(Long flashSaleId, Long productId);
}
