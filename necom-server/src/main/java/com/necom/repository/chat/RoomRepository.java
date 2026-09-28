package com.necom.repository.chat;

import com.necom.entity.chat.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    Optional<Room> findByUserUsername(String username);

    boolean existsByIdAndUserUsername(Long id, String username);

    @Query("SELECT r FROM Room r JOIN FETCH r.user LEFT JOIN FETCH r.assignee "
            + "LEFT JOIN FETCH r.lastMessage lm LEFT JOIN FETCH lm.user "
            + "ORDER BY r.updatedAt DESC")
    List<Room> findAllForInbox();

}
