package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.service.RetrieveUserService;
import org.springframework.stereotype.Component;

@Component
public class UserDataFacade {

    private final RetrieveUserService retrieveUserService;

    public UserDataFacade(final RetrieveUserService retrieveUserService) {
        this.retrieveUserService = retrieveUserService;
    }

    public User retrieveUser(String username){
        return retrieveUserService.retrieveUser(username);
    }
}
