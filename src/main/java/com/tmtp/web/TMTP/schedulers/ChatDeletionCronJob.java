package com.tmtp.web.TMTP.schedulers;

import com.tmtp.web.TMTP.repository.ChatMessageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ChatDeletionCronJob {

    private ChatMessageRepository chatMessageRepository;

    public ChatDeletionCronJob(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Scheduled(fixedRate = 86400000)
    public void reportCurrentTime() {
        chatMessageRepository.deleteAll();
    }
}
