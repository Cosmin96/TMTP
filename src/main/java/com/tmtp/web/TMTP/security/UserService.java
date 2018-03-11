package com.tmtp.web.TMTP.security;

import com.tmtp.web.TMTP.entity.User;

public interface UserService {

    void save(User user);

    User findById(String id);

    User findByUsername(String username);

    User findByEmail(String email);

    void updateUser(User user);

}
