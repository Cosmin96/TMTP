package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.dto.enums.MessageType;
import com.tmtp.web.TMTP.entity.ChatMessage;
import com.tmtp.web.TMTP.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ChatService {

    ChatMessage sendTextMessageInRoom(User user, String text, String room);
    ChatMessage sendMediaMessageInRoom(User user, MultipartFile file, String room, MessageType messageType) throws IOException;
}
