package com.necom.mapper.chat;

import com.necom.dto.chat.MessageRequest;
import com.necom.dto.chat.MessageResponse;
import com.necom.entity.chat.Message;
import com.necom.mapper.GenericMapper;
import com.necom.utils.MapperUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = MapperUtils.class)
public interface MessageMapper extends GenericMapper<Message, MessageRequest, MessageResponse> {

    @Override
    @Mapping(source = "userId", target = "user")
    @Mapping(source = "roomId", target = "room")
    Message requestToEntity(MessageRequest request);

}
