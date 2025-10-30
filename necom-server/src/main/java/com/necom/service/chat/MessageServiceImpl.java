package com.necom.service.chat;

import com.necom.constant.ResourceName;
import com.necom.constant.SearchFields;
import com.necom.dto.ListResponse;
import com.necom.dto.chat.MessageRequest;
import com.necom.dto.chat.MessageResponse;
import com.necom.entity.chat.Message;
import com.necom.mapper.chat.MessageMapper;
import com.necom.repository.authentication.UserRepository;
import com.necom.repository.chat.MessageRepository;
import com.necom.repository.chat.RoomRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class MessageServiceImpl implements MessageService {

    private MessageRepository messageRepository;
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private MessageMapper messageMapper;

    @Override
    public ListResponse<MessageResponse> findAll(int page, int size, String sort, String filter, String search, boolean all) {
        return defaultFindAll(page, size, sort, filter, search, all, SearchFields.MESSAGE, messageRepository, messageMapper);
    }

    @Override
    public MessageResponse findById(Long id) {
        return defaultFindById(id, messageRepository, messageMapper, ResourceName.MESSAGE);
    }

    @Override
    public MessageResponse save(MessageRequest request) {
        Message message = messageMapper.requestToEntity(request);

        userRepository.findById(request.getUserId()).ifPresent(message::setUser);

        // (1) Save message
        Message messageAfterSave = messageRepository.save(message);

        // (2) Save room
        roomRepository.findById(request.getRoomId())
                .ifPresent(room -> {
                    room.setUpdatedAt(Instant.now());
                    room.setLastMessage(messageAfterSave);
                    roomRepository.save(room);
                });

        return messageMapper.entityToResponse(messageAfterSave);
    }

    @Override
    public MessageResponse save(Long id, MessageRequest request) {
        return defaultSave(id, request, messageRepository, messageMapper, ResourceName.MESSAGE);
    }

    @Override
    public void delete(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    public void delete(List<Long> ids) {
        messageRepository.deleteAllById(ids);
    }

}
