package com.tmtp.web.TMTP.repository;

import com.tmtp.web.TMTP.entity.VideoPosts;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VideoPostsRepository extends MongoRepository<VideoPosts, String>{
    List<VideoPosts> findByCreator(String username);
    VideoPosts findById(String id);
}
