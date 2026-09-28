package com.necom.service.chat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChatLogicTests {

    @Test
    void detectsRequestForHumanAgent() {
        assertTrue(ChatService.wantsHuman("Cho mình gặp tư vấn viên"));
        assertTrue(ChatService.wantsHuman("tôi muốn KHIẾU NẠI"));
        assertTrue(ChatService.wantsHuman("nói chuyện với người thật được không"));
        assertFalse(ChatService.wantsHuman("Nhân viên giao hàng tới chưa?"));
        assertFalse(ChatService.wantsHuman("Tư vấn giúp mình sofa"));
    }

    @Test
    void parsesPriceRangeFromVietnameseText() {
        ProductCatalogIndex.PriceRange under = ProductCatalogIndex.parsePriceRange("sofa duoi 15 trieu");
        assertEquals(15_000_000d, under.getMax());
        assertNull(under.getMin());

        assertEquals(500_000d, ProductCatalogIndex.parsePriceRange("den ngu duoi 500k").getMax());
        assertEquals(1_500_000d, ProductCatalogIndex.parsePriceRange("tren 1.5 trieu").getMin());
        assertEquals(2_000_000d, ProductCatalogIndex.parsePriceRange("khong qua 2.000.000 d").getMax());
    }

    @Test
    void cabinetIsNotParsedAsPriceKeyword() {
        // "tủ" và "từ" cùng thành "tu" sau khi bỏ dấu
        ProductCatalogIndex.PriceRange range = ProductCatalogIndex.parsePriceRange("tu 2 canh go soi");
        assertNull(range.getMin());
        assertNull(range.getMax());
    }

}
