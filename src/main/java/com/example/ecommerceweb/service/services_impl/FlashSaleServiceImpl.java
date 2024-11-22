package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.entity.FlashSale;
import com.example.ecommerceweb.repository.FlashSaleRepository;
import com.example.ecommerceweb.service.FlashSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlashSaleServiceImpl implements FlashSaleService {
    private final FlashSaleRepository flashSaleRepository;

    @Override
    public void addFlashSale(FlashSale flashSale) {
        flashSaleRepository.save(flashSale);
    }

    @Override
    public List<FlashSale> getOngoingFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.findAllByStartTimeBeforeAndEndTimeAfter(now, now);
    }

    @Override
    public List<FlashSale> getEndedFlashSales() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.findAllByEndTimeBefore(now);
    }

    @Override
    public FlashSale getFlashSale(Long flashSaleId) {
        Optional<FlashSale> flashSale = flashSaleRepository.findById(flashSaleId);
        return flashSale.orElse(null);
    }

    @Override
    public void updateFlashSale(Long flashSaleId, FlashSale flashSale) {
        FlashSale existingFlashSale = getFlashSale(flashSaleId);
        if (existingFlashSale != null) {
            existingFlashSale.setName(flashSale.getName());
            existingFlashSale.setStartTime(flashSale.getStartTime());
            existingFlashSale.setEndTime(flashSale.getEndTime());
            flashSaleRepository.save(existingFlashSale);
        }
    }

    @Override
    public void deleteFlashSale(Long flashSaleId) {
        flashSaleRepository.deleteById(flashSaleId);
    }

    @Override
    public FlashSale getCurrentFlashSale() {
        LocalDateTime now = LocalDateTime.now();
        return flashSaleRepository.findAllByStartTimeBeforeAndEndTimeAfter(now, now).stream().findFirst().orElse(null);
    }
}
