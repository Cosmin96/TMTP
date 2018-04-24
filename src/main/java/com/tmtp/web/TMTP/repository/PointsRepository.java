package com.tmtp.web.TMTP.repository;


import com.tmtp.web.TMTP.entity.Points;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PointsRepository extends MongoRepository<Points, String> {
}
