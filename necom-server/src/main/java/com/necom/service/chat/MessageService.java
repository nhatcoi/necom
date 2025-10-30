package com.necom.service.chat;

import com.necom.dto.chat.MessageRequest;
import com.necom.dto.chat.MessageResponse;
import com.necom.service.CrudService;

public interface MessageService extends CrudService<Long, MessageRequest, MessageResponse> {}
