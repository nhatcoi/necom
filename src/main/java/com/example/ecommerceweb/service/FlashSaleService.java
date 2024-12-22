package com.example.ecommerceweb.service;

import com.example.ecommerceweb.entity.FlashSale;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FlashSaleService {
    void addFlashSale(FlashSale flashSale);
    List<FlashSale> getOngoingFlashSales();
    List<FlashSale> getEndedFlashSales();
    FlashSale getFlashSale(Long flashSaleId);
    void updateFlashSale(Long flashSaleId, FlashSale flashSale);
    void deleteFlashSale(Long flashSaleId);
    FlashSale getCurrentFlashSale();
}
