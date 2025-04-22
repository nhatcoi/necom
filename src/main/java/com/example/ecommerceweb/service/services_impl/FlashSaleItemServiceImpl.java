package com.example.ecommerceweb.service.services_impl;

import com.example.ecommerceweb.entity.product.FlashSale;
import com.example.ecommerceweb.entity.product.FlashSaleItem;
import com.example.ecommerceweb.repository.FlashSaleItemRepository;
import com.example.ecommerceweb.repository.FlashSaleRepository;
import com.example.ecommerceweb.service.FlashSaleItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlashSaleItemServiceImpl implements FlashSaleItemService {
    private final FlashSaleRepository flashSaleRepository;
    private final FlashSaleItemRepository flashSaleItemRepository;

    @Override
    public void addProductToFlashSale(Long flashSaleId, FlashSaleItem flashSaleItem) {
        Optional<FlashSale> flashSale = flashSaleRepository.findById(flashSaleId);
        if (flashSale.isPresent()) {
            flashSaleItem.setFlashSale(flashSale.get());
            flashSaleItemRepository.save(flashSaleItem);
        }
    }

    @Override
    public List<FlashSaleItem> getProductsInFlashSale(Long flashSaleId) {
        return flashSaleItemRepository.findByFlashSaleId(flashSaleId);
    }

    @Override
    public FlashSaleItem getProductInFlashSale(Long flashSaleId, Long productId) {
        return flashSaleItemRepository.findByFlashSaleIdAndProductSaleId(flashSaleId, productId);
    }

    @Override
    public void updateProductInFlashSale(Long flashSaleId, Long productId, FlashSaleItem flashSaleItem) {
        FlashSaleItem existingItem = getProductInFlashSale(flashSaleId, productId);
        if (existingItem != null) {
            existingItem.setSalePrice(flashSaleItem.getSalePrice());
            existingItem.setQuantityLimit(flashSaleItem.getQuantityLimit());
            flashSaleItemRepository.save(existingItem);
        }
    }

    @Override
    public void deleteProductFromFlashSale(Long flashSaleId, Long productId) {
        FlashSaleItem item = getProductInFlashSale(flashSaleId, productId);
        if (item != null) {
            flashSaleItemRepository.delete(item);
        }
    }

    @Override
    public boolean isProductInFlashSaleValid(Long flashSaleId, Long productId, int quantity) {
        FlashSaleItem item = getProductInFlashSale(flashSaleId, productId);
        return item != null && item.getQuantityLimit() >= quantity;
    }

    @Override
    public void updateProductQuantity(Long flashSaleId, Long productId, int quantity) {
        FlashSaleItem item = getProductInFlashSale(flashSaleId, productId);
        if (item != null && item.getQuantityLimit() >= quantity) {
            item.setQuantityLimit(item.getQuantityLimit() - quantity);
            flashSaleItemRepository.save(item);
        }
    }
}
