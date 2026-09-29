package com.necom.service.chat;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
    void handoffOnlyWhenCustomerNeedsSupport() {
        assertTrue(ChatService.needsHumanSupport("Mình muốn khiếu nại vì hàng giao bị vỡ"));
        assertTrue(ChatService.needsHumanSupport("cho mình đổi trả đơn A1B2 vì giao sai màu"));
        assertTrue(ChatService.needsHumanSupport("Mình muốn đổi sang màu khác"));
        assertTrue(ChatService.needsHumanSupport("Hoàn tiền giúp mình với"));
        assertFalse(ChatService.needsHumanSupport("Mình cần ghế ngồi làm việc lâu không mỏi lưng"));
        assertFalse(ChatService.needsHumanSupport("Có sofa nào dưới 6 triệu không?"));
        assertFalse(ChatService.needsHumanSupport("Chính sách đổi trả thế nào?"));
        assertFalse(ChatService.needsHumanSupport("Sofa này bảo hành bao lâu?"));
    }

}
