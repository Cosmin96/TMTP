package com.tmtp.web.TMTP.web.mobile;

import com.tmtp.web.TMTP.entity.Team;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.UserInfo;
import com.tmtp.web.TMTP.dto.exceptions.BadFormatException;
import com.tmtp.web.TMTP.dto.exceptions.NoUserFound;
import com.tmtp.web.TMTP.security.SecurityService;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.utils.RequestValidator;
import com.tmtp.web.TMTP.web.UserDataFacade;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

@RestController
public class IOSController {

    private final UserService userService;
    private final SecurityService securityService;
    private final RequestValidator requestValidator;
    private final UserDataFacade userDataFacade;
    private final MessageSource messageSource;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public IOSController(final UserService userService,
                         final SecurityService securityService,
                         final RequestValidator requestValidator,
                         final UserDataFacade userDataFacade,
                         final MessageSource messageSource,
                         final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.securityService = securityService;
        this.requestValidator = requestValidator;
        this.userDataFacade = userDataFacade;
        this.messageSource = messageSource;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @RequestMapping(value = "/mobile/register", method = RequestMethod.POST)
    public UserInfo registerUserFromApp(@RequestBody UserInfo userInfo){

        String errorMessage = requestValidator.validateRegistration(userInfo);
        if(errorMessage != null) {
            throw new BadFormatException(errorMessage);
        }

        userInfo.setUsername(userInfo.getUsername().replaceAll(" ",""));
        userInfo.setUsername(userInfo.getUsername().replaceAll("[^\\w\\s]+",""));
        userInfo.setUsername(userInfo.getUsername().toLowerCase());

        User user = new User();
        user.setUsername(userInfo.getUsername());
        user.setPassword(userInfo.getPassword());
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setEmail(userInfo.getEmail());
        user.setProfile("no");
        user.setTeam(Team.ARSENAL);

        userService.save(user);
        securityService.autologin(userInfo.getUsername(), userInfo.getPassword());

        userInfo.setPassword("");
        userInfo.setSessionID(RequestContextHolder.currentRequestAttributes().getSessionId());

        return userInfo;
    }

    @RequestMapping(value = "/mobile/login", method = RequestMethod.POST)
    public UserInfo loginUserFromApp(@RequestBody UserInfo userInfo){
        User user = userDataFacade.retrieveUser(userInfo.getUsername());

        if(user == null || !bCryptPasswordEncoder.matches(userInfo.getPassword(), user.getPassword())){
            throw new NoUserFound(getMessage("INVALID_CREDENTIALS"));
        }

        securityService.autologin(userInfo.getUsername(), userInfo.getPassword());

        userInfo.setEmail(user.getEmail());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setPassword("");
        userInfo.setSessionID(RequestContextHolder.currentRequestAttributes().getSessionId());

        return userInfo;
    }

    private String getMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, null);
    }
}
