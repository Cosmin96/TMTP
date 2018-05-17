package com.tmtp.web.TMTP.web;

import com.cloudinary.utils.StringUtils;
import com.tmtp.web.TMTP.dto.exceptions.UnauthorisedAccess;
import com.tmtp.web.TMTP.entity.TokenInfo;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.RetrieveUserService;
import com.tmtp.web.TMTP.service.TokenInfoService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserDataFacade {

    private final RetrieveUserService retrieveUserService;
    private final UserService userService;
    private final TokenInfoService tokenInfoService;

    public UserDataFacade(final RetrieveUserService retrieveUserService,
                          final TokenInfoService tokenInfoService,
                          final UserService userService) {
        this.retrieveUserService = retrieveUserService;
        this.tokenInfoService = tokenInfoService;
        this.userService = userService;
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

    public void updateUser(User user){
        userService.updateUser(user);
    }

    public User getUserFromAuthHeader(String authHeader) {

        User user = null;

        if(StringUtils.isNotBlank(authHeader) && authHeader.contains("bearer")) {

            String tokenValue = authHeader.replace("bearer", "");
            tokenValue = tokenValue.trim();
            TokenInfo tokenInfo = tokenInfoService.getTokenFromTokenString(tokenValue);
            if(tokenInfo != null
                    && StringUtils.isNotBlank(tokenInfo.getUserName())
                    && userService.findByUsername(tokenInfo.getUserName()) != null) {

                //extract user from the token
                user = userService.findByUsername(tokenInfo.getUserName());
            }
        }

        if(user != null) {
            return user;
        } else {
            throw new UnauthorisedAccess();
        }
    }
}
