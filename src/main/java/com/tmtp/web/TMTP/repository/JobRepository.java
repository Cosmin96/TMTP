package com.tmtp.web.TMTP.repository;

import com.tmtp.web.TMTP.entity.Job;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JobRepository extends MongoRepository<Job, String> {

    public Job findById(String id);
}
