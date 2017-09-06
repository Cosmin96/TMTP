package com.tmtp.web.TMTP.security;

import com.tmtp.web.TMTP.entity.User;

public interface UserService {

    void save(User user);

    User findByUsername(String username);
}
