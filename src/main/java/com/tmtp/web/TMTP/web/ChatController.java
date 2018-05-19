package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.dto.AppResponse;
import com.tmtp.web.TMTP.dto.enums.MessageType;
import com.tmtp.web.TMTP.dto.exceptions.UserBannedException;
import com.tmtp.web.TMTP.entity.ChatMessage;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.repository.ChatMessageRepository;
import com.tmtp.web.TMTP.service.ChatService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
public class ChatController {

    private final ChatService chatService;
    private final UserDataFacade userDataFacade;
    private final ChatMessageRepository chatMessageRepository;
    private final MessageSource messageSource;

    public ChatController(
            final ChatService chatService,
            final UserDataFacade userDataFacade,
            final ChatMessageRepository chatMessageRepository,
            final MessageSource messageSource) {
        this.chatService = chatService;
        this.userDataFacade = userDataFacade;
        this.chatMessageRepository = chatMessageRepository;
        this.messageSource = messageSource;
    }

    private boolean isRoomOk(String room) {
        if (room == null) {
            return false;
        } else return room.length() > 0;

        // More verification can be added here if needed
    }

    @RequestMapping(value = "/new-chat-message/{text}/{room}", method = RequestMethod.POST)
    @ResponseBody
    public String sendMessageToRoom(
            @PathVariable("text") String text, @PathVariable("room") String room) {
        User user = userDataFacade.retrieveLoggedUser();
        if (user.getBanned()) {
            return getMessage("FORBIDDEN_REQUEST");
        }

        if (text == null) {
            return getMessage("INVALID_REQUEST");
        }

        // Decode base64
        text = StringUtils.newStringUtf8(Base64.decodeBase64(text));
        if (text.length() <= 0 || text.length() > 1000) {
            return getMessage("INVALID_REQUEST");
        }

        //Check / create chat room
        if (!isRoomOk(room)) {
            return getMessage("INVALID_REQUEST");
        }

        chatService.sendTextMessageInRoom(user, text, room);
        return "Sent";
    }

    @RequestMapping(value = "/media-message/{room}", method = RequestMethod.POST)
    @ResponseBody
    public AppResponse sendMediaMessageToRoom(
            @RequestParam("file") MultipartFile file, @PathVariable("room") String room,
            @RequestParam MessageType messageType) throws IOException {
        User user = userDataFacade.retrieveLoggedUser();

        if (user.getBanned()) {
            throw new UserBannedException(getMessage("Banned.userForm.username"));
        }

        //Check / create chat room
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
        //Check / create chat room
        if (!isRoomOk(room)) {
            return null;
        }

        int requireNMessages = 30;
        List<ChatMessage> chatMessages = chatMessageRepository.findByName(room);
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