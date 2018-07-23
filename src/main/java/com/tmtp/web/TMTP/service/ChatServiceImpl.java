package com.tmtp.web.TMTP.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pusher.rest.Pusher;
import com.pusher.rest.data.Result;
import com.tmtp.web.TMTP.dto.CloudinaryObject;
import com.tmtp.web.TMTP.dto.enums.FileType;
import com.tmtp.web.TMTP.dto.enums.MessageType;
import com.tmtp.web.TMTP.entity.ChatMessage;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.repository.ChatMessageRepository;
import com.tmtp.web.TMTP.service.cloud.CloudStorageService;

@Service
public class ChatServiceImpl implements ChatService {
    private static final Logger LOG = LoggerFactory.getLogger(ChatServiceImpl.class);

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
        //TODO Why new instance of pusher is created every time? should it be a long-living object?
        LOG.debug("About to push [{}] message to [{}] channel", messageType.getText(), room);
        Pusher pusher = new Pusher(pusherAppId, pusherAppKey, pusherSecretKey);
        pusher.setCluster(pusherCluster);
        pusher.setEncrypted(true);

        Map<String, String> map = new HashMap<>();
        map.put("message", text);
        map.put("username", user.getUsername());
        map.put("type", messageType.getText());

        if(LOG.isDebugEnabled()) {
            LOG.debug("Triggering \"new-message\" event to the channel [{}] with contents:\n {}", room, map);
        }
        
        Result pushResult = pusher.trigger(room, "new-message", map);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Push result: [status={}; message={}; httpStatus={}]", pushResult.getStatus().name(), pushResult.getMessage(), pushResult.getHttpStatus());
        }

        LOG.debug("Archiving chat message...");
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setDateTime(DateTime.now());
        chatMessage.setName(room);
        chatMessage.setText(text);
        chatMessage.setUsername(user.getUsername());
        chatMessage.setMessageType(messageType);

        // Save message in database
        ChatMessage saved = chatMessageRepository.save(chatMessage);
        LOG.debug("Archived [id={}]", saved.getId());
        return saved;
    }
}
