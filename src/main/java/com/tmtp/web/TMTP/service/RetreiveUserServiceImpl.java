package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RetreiveUserServiceImpl implements RetrieveUserService {

    private final UserRepository userRepository;

    public RetreiveUserServiceImpl(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User retrieveUser(String username){
        return userRepository.findByUsername(username);
    }
}
