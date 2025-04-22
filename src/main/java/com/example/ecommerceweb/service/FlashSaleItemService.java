package com.example.ecommerceweb.service;

import com.example.ecommerceweb.entity.product.FlashSaleItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FlashSaleItemService {
    void addProductToFlashSale(Long flashSaleId, FlashSaleItem flashSaleItem); // Cập nhật để dùng đối tượng FlashSaleItem

    List<FlashSaleItem> getProductsInFlashSale(Long flashSaleId);

    FlashSaleItem getProductInFlashSale(Long flashSaleId, Long productId); // Trả về FlashSaleItem thay vì Product để có thêm thông tin chi tiết về giá giảm

    void updateProductInFlashSale(Long flashSaleId, Long productId, FlashSaleItem flashSaleItem); // Dùng FlashSaleItem để cập nhật giá và số lượng

    void deleteProductFromFlashSale(Long flashSaleId, Long productId); // Đổi tên từ deleteProductInFlashSale

    boolean isProductInFlashSaleValid(Long flashSaleId, Long productId, int quantity); // Cập nhật để kiểm tra số lượng và tính hợp lệ

    void updateProductQuantity(Long flashSaleId, Long productId, int quantity);
}
