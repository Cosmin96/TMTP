package com.tmtp.web.TMTP.schedulers;

import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.tmtp.web.TMTP.entity.ChatMessage;
import com.tmtp.web.TMTP.repository.ChatMessageRepository;
import com.tmtp.web.TMTP.web.ChatController;

@Component
public class ChatDeletionCronJob {
    private static final Logger LOG = LoggerFactory.getLogger(ChatController.class);

    private static final long TASK_PERIOD = 1000 * 60 * 60 * 24; // milliseconds, 24 hours
    private static final long TASK_DELAY = 0;  // milliseconds
    private static final int MESSAGE_LIVE_TIME = 60 * 60 * 24 * 10; // seconds, 10 days
    
    private ChatMessageRepository chatMessageRepository;

    public ChatDeletionCronJob(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Scheduled(fixedRate = TASK_PERIOD, initialDelay = TASK_DELAY)
    public void run() {
        // TODO make sure no audio messages removed from database
        // before corresponding audio file is removed from cloud storage
        DateTime cutDownDate = DateTime.now().minusSeconds(MESSAGE_LIVE_TIME);
        List<ChatMessage> oldMessages = chatMessageRepository.findByDateTimeBefore(cutDownDate);
        LOG.info("Deleting chat messages dated before {}: found {} message(s)", cutDownDate, oldMessages.size());
        chatMessageRepository.delete(oldMessages);
    }
}
