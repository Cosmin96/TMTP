package com.tmtp.web.TMTP.web;

import com.pusher.rest.Pusher;
import com.tmtp.web.TMTP.entity.ChatMessage;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.repository.ChatMessageRepository;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ChatController {
    @Value("${PUSHER_APP_ID}")
    private String pusherAppId;

    @Value("${PUSHER_APP_KEY}")
    private String pusherAppKey;

    @Value("${PUSHER_SECRET_KEY}")
    private String pusherSecretKey;

    @Value("${PUSHER_CLUSTER}")
    private String pusherCluster;

    private final UserDataFacade userDataFacade;
    private final ChatMessageRepository chatMessageRepository;

    public ChatController(final UserDataFacade userDataFacade, final ChatMessageRepository chatMessageRepository) {
        this.userDataFacade = userDataFacade;
        this.chatMessageRepository = chatMessageRepository;
    }

    private boolean isRoomOk(String room) {
        if(room == null) {
            return false;
        } else if(room.length() <= 0) {
            return false;
        }

        // More verification can be added here if needed

        return true;
    }

    @RequestMapping(value = "/new-chat-message/{text}/{room}", method = RequestMethod.GET)
    @ResponseBody
    public String createNewPost(@PathVariable("text") String text, @PathVariable("room") String room){
        User user = userDataFacade.retrieveLoggedUser();
        if(user.getBanned()){
            return "Forbidden!";
        }

        if(text == null) {
            return "Invalid request!";
        }

        // Decode base64
        text = StringUtils.newStringUtf8(Base64.decodeBase64(text));
        if(text.length() <= 0 || text.length() > 1000) {
            return "Invalid request!";
        }

        //Check / create chat room
        if(isRoomOk(room) == false) {
            return "Invalid request!";
        }

        Pusher pusher = new Pusher(pusherAppId, pusherAppKey, pusherSecretKey);
        pusher.setCluster(pusherCluster);
        pusher.setEncrypted(true);

        Map<String, String> map = new HashMap<String, String>();
        map.put("message", text);
        map.put("username", user.getUsername());

        pusher.trigger(room, "new-message", map);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setDateTime(DateTime.now());
        chatMessage.setName(room);
        chatMessage.setText(text);
        chatMessage.setUsername(user.getUsername());

        // Save message in database
        chatMessageRepository.save(chatMessage);

        return "Sent";
    }


    @RequestMapping(value = "/get-next-messages/{room}/{start}/{end}", method = RequestMethod.GET)
    @ResponseBody
    public List<ChatMessage> getNextBatch(@PathVariable String room, @PathVariable int start, @PathVariable int end)
    {
        //Check / create chat room
        if(isRoomOk(room) == false) {
            return null;
        }

        int requireNMessages = 30;
        List<ChatMessage> chatMessages = chatMessageRepository.findByName(room);
        int total = chatMessages.size();

        if(start > end) { return null; }
        if(start < 0 || start > total) { return null; }
        if(end < 0 || end > total) { return null; }

        return chatMessages.subList(start, end);
    }
}
