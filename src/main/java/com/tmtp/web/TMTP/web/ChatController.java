package com.tmtp.web.TMTP.web;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.tmtp.web.TMTP.dto.AppResponse;
import com.tmtp.web.TMTP.dto.enums.MessageType;
import com.tmtp.web.TMTP.dto.exceptions.UserBannedException;
import com.tmtp.web.TMTP.entity.ChatMessage;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.repository.ChatMessageRepository;
import com.tmtp.web.TMTP.service.ChatService;

@Controller
public class ChatController {
    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;
    private final UserDataFacade userDataFacade;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageSource messageSource;

    public ChatController(final ChatService chatService, final UserDataFacade userDataFacade, final ChatMessageRepository chatMessageRepository,
            final MessageSource messageSource) {
        this.chatService = chatService;
        this.userDataFacade = userDataFacade;
        this.chatMessageRepository = chatMessageRepository;
        this.messageSource = messageSource;
    }

    private boolean isRoomOk(String room) {
        if (room == null) {
            return false;
        } else
            return room.length() > 0;
    }

    @RequestMapping(value = "/new-chat-message/{text}/{room}", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessageToRoom(@PathVariable("text") String text, @PathVariable("room") String room) {

        LOG.debug("Recieved request about new text message");
        LOG.debug("Checking pre-conditions...");

        LOG.debug("Checking for empty text...");
        if (text == null) {
            return getMessage("INVALID_REQUEST");
        }

        LOG.debug("Checking user...");
        User user = userDataFacade.retrieveLoggedUser();
        if (user.getBanned()) {
            return getMessage("FORBIDDEN_REQUEST");
        }

        // Check / create chat room
        LOG.debug("Checking room...");
        if (!isRoomOk(room)) {
            return getMessage("INVALID_REQUEST");
        }

        LOG.debug("Decoding BASE64 message...");
        text = StringUtils.newStringUtf8(Base64.decodeBase64(text));
        LOG.debug("Decoded message -> {}", text);
        if (text.length() <= 0 || text.length() > 1000) {
            return getMessage("INVALID_REQUEST");
        }

        chatService.sendTextMessageInRoom(user, text, room);
        return "Sent";
    }

    @RequestMapping(value = "/audio-message/{room}", method = RequestMethod.POST, headers = "content-type=multipart/form-data")
    @ResponseBody
    public AppResponse sendAudioMediaMessageToRoom(MultipartHttpServletRequest request, @PathVariable("room") String room, @RequestParam MessageType messageType)
            throws IOException {
        LOG.debug("Recieved request about new [{}] message", messageType.getText());
        LOG.debug("Checking pre-conditions...");

        LOG.debug("Checking user...");
        User user = userDataFacade.retrieveLoggedUser();
        if (user.getBanned()) {
            throw new UserBannedException(getMessage("Banned.userForm.username"));
        }

        LOG.debug("Checking room...");
        if (!isRoomOk(room)) {
            throw new IllegalArgumentException(getMessage("INVALID_REQUEST"));
        }

        MultipartFile file = request.getFile("file");
        if(LOG.isDebugEnabled()) {
            LOG.debug("Received [{} kB]", file.getSize() >> 10);
        }
        AppResponse response = new AppResponse();
        response.setData(chatService.sendMediaMessageInRoom(user, file, room, messageType));
        return response;
    }

    @RequestMapping(value = "/media-message/{room}", method = RequestMethod.POST)
    @ResponseBody
    public AppResponse sendMediaMessageToRoom(@RequestParam("file") MultipartFile file, @PathVariable("room") String room, @RequestParam MessageType messageType)
            throws IOException {
        User user = userDataFacade.retrieveLoggedUser();

        if (user.getBanned()) {
            throw new UserBannedException(getMessage("Banned.userForm.username"));
        }

        // Check / create chat room
        if (!isRoomOk(room)) {
            throw new IllegalArgumentException(getMessage("INVALID_REQUEST"));
        }

        AppResponse response = new AppResponse();
        response.setData(chatService.sendMediaMessageInRoom(user, file, room, messageType));
        return response;
    }

    @RequestMapping(value = "/get-next-messages/{room}/{start}/{end}", method = RequestMethod.GET)
    @ResponseBody
    public List<ChatMessage> getNextBatch(@PathVariable String room, @PathVariable int start, @PathVariable int end) {
        // Check / create chat room
        if (!isRoomOk(room)) {
            return null;
        }

        int requireNMessages = 30;
        List<ChatMessage> chatMessages = chatMessageRepository.findByName(room);
        // TODO this should be sorted from older to newer
        Collections.sort(chatMessages);

        int total = chatMessages.size();

        if (start > end) {
            return null;
        }
        if (start < 0 || start > total) {
            return null;
        }
        if (end < 0 || end > total) {
            return null;
        }

        return chatMessages.subList(start, end);
    }

    private String getMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, null);
    }
}