package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.service.RetrieveUserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDataFacade {

    private final RetrieveUserService retrieveUserService;

    public UserDataFacade(final RetrieveUserService retrieveUserService) {
        this.retrieveUserService = retrieveUserService;
    }

    public User retrieveLoggedUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return retrieveUserService.retrieveUser(username);
    }

    public User retrieveUser(String username){
        return retrieveUserService.retrieveUser(username);
    }

    public List<User> retrieveAllUsers(){
        return retrieveUserService.retrieveAllUsers();
    }

    public void deleteUser(User user){
        retrieveUserService.deleteUser(user);
    }
}
