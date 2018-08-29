package com.tmtp.web.TMTP.repository;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tmtp.web.TMTP.dto.enums.MessageType;
import com.tmtp.web.TMTP.entity.ChatMessage;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByName(String name);

    List<ChatMessage> findByMessageTypeAndDateTimeBefore(MessageType messageType, DateTime dateTime);

    List<ChatMessage> findByDateTimeBefore(DateTime dateTime);
}