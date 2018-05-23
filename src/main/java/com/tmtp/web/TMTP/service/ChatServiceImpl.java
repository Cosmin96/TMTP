package com.tmtp.web.TMTP.service;

import com.pusher.rest.Pusher;
import com.tmtp.web.TMTP.dto.CloudinaryObject;
import com.tmtp.web.TMTP.dto.enums.FileType;
import com.tmtp.web.TMTP.dto.enums.MessageType;
import com.tmtp.web.TMTP.entity.ChatMessage;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.repository.ChatMessageRepository;
import com.tmtp.web.TMTP.service.cloud.CloudStorageService;
import com.tmtp.web.TMTP.web.UserDataFacade;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class ChatServiceImpl implements ChatService {

    @Value("${PUSHER_APP_ID}")
    private String pusherAppId;

    @Value("${PUSHER_APP_KEY}")
    private String pusherAppKey;

    @Value("${PUSHER_SECRET_KEY}")
    private String pusherSecretKey;

    @Value("${PUSHER_CLUSTER}")
    private String pusherCluster;

    @Value("${cloudinary.voice-notes.folder}")
    private String audioBucket;

    private final CloudStorageService cloudStorage;
    private final ChatMessageRepository chatMessageRepository;

    public ChatServiceImpl(
            final CloudStorageService cloudStorage,
            final ChatMessageRepository chatMessageRepository) {
        this.cloudStorage = cloudStorage;
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    public ChatMessage sendTextMessageInRoom(User user, String text, String room) {
        return pushMessageToPusher(user, text, room, MessageType.TEXT);
    }

    @Override
    public ChatMessage sendMediaMessageInRoom(
            User user, MultipartFile file, String room, MessageType messageType)
            throws IOException {

        CloudinaryObject cloudinaryObject = cloudStorage.uploadFile(file, FileType.AUDIO, audioBucket);
        String secureUrl = cloudinaryObject.getSecureUrl();
        return pushMessageToPusher(user, secureUrl, room, messageType);
    }

    private ChatMessage pushMessageToPusher(User user, String text, String room, MessageType messageType) {
        Pusher pusher = new Pusher(pusherAppId, pusherAppKey, pusherSecretKey);
        pusher.setCluster(pusherCluster);
        pusher.setEncrypted(true);

        Map<String, String> map = new HashMap<>();
        map.put("message", text);
        map.put("username", user.getUsername());
        map.put("type", messageType.getText());

        pusher.trigger(room, "new-message", map);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setDateTime(DateTime.now());
        chatMessage.setName(room);
        chatMessage.setText(text);
        chatMessage.setUsername(user.getUsername());
        chatMessage.setMessageType(messageType);

        // Save message in database
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }
}
