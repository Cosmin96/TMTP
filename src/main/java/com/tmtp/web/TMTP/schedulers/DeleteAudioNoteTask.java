package com.tmtp.web.TMTP.schedulers;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.tmtp.web.TMTP.dto.enums.MessageType;
import com.tmtp.web.TMTP.entity.ChatMessage;
import com.tmtp.web.TMTP.repository.ChatMessageRepository;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DeleteAudioNoteTask {

    private static final Logger LOG = LoggerFactory.getLogger(DeleteAudioNoteTask.class);

    private final ChatMessageRepository chatMessageRepository;
    private final Cloudinary cloudinary;

    private final long MILLISECONDS_IN_DAY = 60 * 60 * 24 * 1000;

    @Autowired
    public DeleteAudioNoteTask(ChatMessageRepository chatMessageRepository,
                               Cloudinary cloudinary) {
        this.chatMessageRepository = chatMessageRepository;
        this.cloudinary = cloudinary;
    }

    @Scheduled(fixedRate = MILLISECONDS_IN_DAY)
    public void deleteAudioNote() {
        LOG.info("Time: {}", LocalDateTime.now());

        DateTime timeBefore24Hours = DateTime.now().minusHours(24);
        List<ChatMessage> messagesToDelete = chatMessageRepository.findByMessageTypeAndDateTimeBefore(
                MessageType.AUDIO, timeBefore24Hours);

        LOG.info("Need to delete {} messages.", messagesToDelete.size());

        //Refer https://cloudinary.com/documentation/java_image_upload#update_and_delete_images
        messagesToDelete.forEach(chatMessage -> {
            try {
                LOG.info("Deleting chat messages with details: \n\tID: {}, \n\tText/Public ID: {}, \n\tType: {}.",
                        chatMessage.getId(), chatMessage.getText(), chatMessage.getMessageType());
                cloudinary.uploader().destroy(
                        chatMessage.getText(), ObjectUtils.asMap("invalidate", true));
            } catch (IOException e) {
                LOG.error("Error in deleting Message on Cloudinary. \n\t ID: {}, \n\t Type: {}.",
                        chatMessage.getText(), chatMessage.getMessageType());
            }
        });

        for(ChatMessage message : messagesToDelete) {
            chatMessageRepository.delete(message);
        }
    }
}
