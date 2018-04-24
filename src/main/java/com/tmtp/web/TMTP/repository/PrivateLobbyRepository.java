package com.tmtp.web.TMTP.repository;

import com.tmtp.web.TMTP.entity.PrivateLobby;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PrivateLobbyRepository extends MongoRepository<PrivateLobby, String> {
    PrivateLobby findById(String id);
    PrivateLobby findByCreator(String creator);
}
