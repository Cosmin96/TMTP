package com.tmtp.web.TMTP.repository;

import com.tmtp.web.TMTP.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    public List<ChatMessage> findByName(String name);
}
