package com.example.ecommerceweb.service;

import com.example.ecommerceweb.entity.FlashSale;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FlashSaleService {
    // Thêm chương trình Flash Sale mới
    void addFlashSale(FlashSale flashSale);

    // Lấy danh sách các chương trình Flash Sale đang diễn ra
    List<FlashSale> getOngoingFlashSales();

    // Lấy danh sách các chương trình Flash Sale đã kết thúc
    List<FlashSale> getEndedFlashSales();

    // Lấy thông tin chi tiết của một chương trình Flash Sale
    FlashSale getFlashSale(Long flashSaleId);

    // Cập nhật thông tin của một chương trình Flash Sale
    void updateFlashSale(Long flashSaleId, FlashSale flashSale);

    // Xóa một chương trình Flash Sale
    void deleteFlashSale(Long flashSaleId);

    FlashSale getCurrentFlashSale();
}
