package com.tmtp.web.TMTP.repository;

import com.tmtp.web.TMTP.entity.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {

}
