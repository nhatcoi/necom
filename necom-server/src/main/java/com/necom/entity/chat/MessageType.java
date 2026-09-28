package com.necom.entity.chat;

public enum MessageType {
    TEXT,
    // Dòng thông báo giữa khung chat: "Lan đã tham gia", "Phiên đã kết thúc"...
    SYSTEM,
    // Ghi chú nội bộ giữa nhân viên, không bao giờ gửi cho khách
    INTERNAL_NOTE
}
