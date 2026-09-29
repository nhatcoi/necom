package com.necom.service.chat.search;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductSearchIndexTests {

    @Test
    void parsesPriceRangeFromVietnameseText() {
        ProductSearchIndex.PriceRange under = ProductSearchIndex.parsePriceRange("sofa duoi 15 trieu");
        assertEquals(15_000_000d, under.getMax());
        assertNull(under.getMin());

        assertEquals(500_000d, ProductSearchIndex.parsePriceRange("den ngu duoi 500k").getMax());
        assertEquals(1_500_000d, ProductSearchIndex.parsePriceRange("tren 1.5 trieu").getMin());
        assertEquals(2_000_000d, ProductSearchIndex.parsePriceRange("khong qua 2.000.000 d").getMax());
    }

    @Test
    void cabinetIsNotParsedAsPriceKeyword() {
        // "tủ" và "từ" cùng thành "tu" sau khi bỏ dấu
        ProductSearchIndex.PriceRange range = ProductSearchIndex.parsePriceRange("tu 2 canh go soi");
        assertNull(range.getMin());
        assertNull(range.getMax());
    }

    @Test
    void queryTermsDropStopwordsAndKeepProductWords() {
        List<String> terms = ProductSearchIndex.queryTerms("Mình muốn mua ghế sofa màu xám cho phòng khách");
        assertTrue(terms.contains("sofa"));
        assertTrue(terms.contains("xam"));
        assertFalse(terms.contains("minh"));
        assertFalse(terms.contains("muon"));
    }

    @Test
    void priceRangeOverlapsVariantRange() {
        ProductSearchIndex.PriceRange under5m = ProductSearchIndex.parsePriceRange("duoi 5 trieu");
        assertTrue(under5m.overlaps(4_500_000d, 6_000_000d));
        assertFalse(under5m.overlaps(5_500_000d, 7_000_000d));
    }

}
