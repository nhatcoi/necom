package com.necom.entity.chat;

public enum RoomStatus {
    // Trợ lý AI đang trả lời
    BOT,
    // Khách yêu cầu gặp tư vấn viên, chưa ai nhận
    WAITING_AGENT,
    // Tư vấn viên đang phụ trách
    AGENT,
    // Phiên đã kết thúc, khách nhắn lại sẽ mở phiên mới với bot
    RESOLVED
}
