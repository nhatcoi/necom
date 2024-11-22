package com.example.ecommerceweb.service;

import com.example.ecommerceweb.entity.FlashSaleItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FlashSaleItemService {
    // Thêm sản phẩm vào Flash Sale
    void addProductToFlashSale(Long flashSaleId, FlashSaleItem flashSaleItem); // Cập nhật để dùng đối tượng FlashSaleItem

    // Lấy danh sách các sản phẩm trong Flash Sale
    List<FlashSaleItem> getProductsInFlashSale(Long flashSaleId);

    // Lấy sản phẩm chi tiết trong Flash Sale (theo ID sản phẩm)
    FlashSaleItem getProductInFlashSale(Long flashSaleId, Long productId); // Trả về FlashSaleItem thay vì Product để có thêm thông tin chi tiết về giá giảm

    // Cập nhật thông tin sản phẩm trong Flash Sale (thay đổi giá hoặc số lượng)
    void updateProductInFlashSale(Long flashSaleId, Long productId, FlashSaleItem flashSaleItem); // Dùng FlashSaleItem để cập nhật giá và số lượng

    // Xóa một sản phẩm khỏi Flash Sale
    void deleteProductFromFlashSale(Long flashSaleId, Long productId); // Đổi tên từ deleteProductInFlashSale

    // Kiểm tra Flash Sale khi thêm sản phẩm vào giỏ hàng
    boolean isProductInFlashSaleValid(Long flashSaleId, Long productId, int quantity); // Cập nhật để kiểm tra số lượng và tính hợp lệ

    // Cập nhật số lượng sản phẩm khi đặt hàng thành công
    void updateProductQuantity(Long flashSaleId, Long productId, int quantity);
}
