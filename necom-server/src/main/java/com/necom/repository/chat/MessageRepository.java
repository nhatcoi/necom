package com.necom.repository.chat;

import com.necom.entity.chat.Message;
import com.necom.entity.chat.SenderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {

    Page<Message> findByRoomId(Long roomId, Pageable pageable);

    Optional<Message> findByClientMsgId(String clientMsgId);

    /**
     * Lấy tin theo phòng, sắp xếp id giảm dần.
     * beforeId: phân trang cuộn ngược; afterId: lấy tin bị lỡ khi kết nối lại.
     * includeInternal = false dùng cho phía khách (loại bỏ ghi chú nội bộ).
     */
    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.user "
            + "WHERE m.room.id = :roomId "
            + "AND (:beforeId IS NULL OR m.id < :beforeId) "
            + "AND (:afterId IS NULL OR m.id > :afterId) "
            + "AND (:includeInternal = TRUE OR m.type <> com.necom.entity.chat.MessageType.INTERNAL_NOTE) "
            + "ORDER BY m.id DESC")
    List<Message> findForRoom(@Param("roomId") Long roomId,
                              @Param("beforeId") Long beforeId,
                              @Param("afterId") Long afterId,
                              @Param("includeInternal") boolean includeInternal,
                              Pageable pageable);

    @Query("SELECT COUNT(m) FROM Message m "
            + "WHERE m.room.id = :roomId AND m.id > :lastReadId AND m.senderType IN :senderTypes "
            + "AND m.type <> com.necom.entity.chat.MessageType.INTERNAL_NOTE")
    long countUnread(@Param("roomId") Long roomId,
                     @Param("lastReadId") Long lastReadId,
                     @Param("senderTypes") Collection<SenderType> senderTypes);

    // Số tin khách gửi mà nhân viên chưa đọc, gom theo phòng: [roomId, count]
    @Query("SELECT m.room.id, COUNT(m) FROM Message m JOIN m.room r "
            + "WHERE m.senderType = com.necom.entity.chat.SenderType.CUSTOMER "
            + "AND m.id > COALESCE(r.agentLastReadId, 0) "
            + "GROUP BY m.room.id")
    List<Object[]> countAgentUnreadGroupByRoom();

}
