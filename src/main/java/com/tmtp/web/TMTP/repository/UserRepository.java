package com.tmtp.web.TMTP.repository;


import com.tmtp.web.TMTP.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    public  User findById(String id);
    public User findByFirstName(String firstName);
    public List<User> findByLastName(String lastName);
    public User findByEmail(String email);
    public User findByUsername(String username);
}
