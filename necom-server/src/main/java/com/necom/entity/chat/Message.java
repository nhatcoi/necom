package com.necom.entity.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.necom.entity.BaseEntity;
import com.necom.entity.authentication.User;
import com.necom.utils.JsonNodeConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "message")
public class Message extends BaseEntity {
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT")
    private Integer status;

    @ManyToOne(fetch = FetchType.LAZY)
    // Null với tin của bot và tin hệ thống
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference
    private Room room;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType type = MessageType.TEXT;

    @Column(name = "sender_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SenderType senderType;

    // Dữ liệu kèm theo để render: products, orders, quickReplies, agentName...
    @Column(name = "payload", columnDefinition = "JSON")
    @Convert(converter = JsonNodeConverter.class)
    private JsonNode payload;

    // Id do client sinh ra, chống lưu trùng khi gửi lại lúc mất kết nối
    @Column(name = "client_msg_id", unique = true)
    private String clientMsgId;
}
