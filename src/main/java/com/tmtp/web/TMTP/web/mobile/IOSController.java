package com.tmtp.web.TMTP.web.mobile;

import com.tmtp.web.TMTP.dto.AppResponse;
import com.tmtp.web.TMTP.dto.exceptions.BadFormatException;
import com.tmtp.web.TMTP.dto.exceptions.NoUserFound;
import com.tmtp.web.TMTP.dto.exceptions.UserBannedException;
import com.tmtp.web.TMTP.entity.Team;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.UserInfo;
import com.tmtp.web.TMTP.security.SecurityService;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.StorageService;
import com.tmtp.web.TMTP.utils.RequestValidator;
import com.tmtp.web.TMTP.web.UserDataFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
public class IOSController {

    private static final Logger LOG = LoggerFactory.getLogger(IOSController.class);

    private final UserService userService;
    private final SecurityService securityService;
    private final RequestValidator requestValidator;
    private final UserDataFacade userDataFacade;
    private final MessageSource messageSource;
    private final StorageService storageService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public IOSController(final UserService userService,
                         final SecurityService securityService,
                         final RequestValidator requestValidator,
                         final UserDataFacade userDataFacade,
                         final MessageSource messageSource,
                         final StorageService storageService,
                         final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.securityService = securityService;
        this.requestValidator = requestValidator;
        this.userDataFacade = userDataFacade;
        this.messageSource = messageSource;
        this.storageService = storageService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @RequestMapping(value = "/mobile/register", method = RequestMethod.POST)
    public AppResponse registerUserFromApp(
            @RequestBody UserInfo userInfo,
            @RequestParam("file") MultipartFile file,
            @RequestParam("inviteCode") String inviteCode){

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

        if(!file.isEmpty()) {
            String photoName = storageService.store(file, userInfo.getUsername());
            LOG.info("Username {}, photo saved: {}.", userInfo.getUsername(), photoName);
        }

        if(inviteCode != null) {
            // Check if code belongs to any user
            User referredUser = userService.findById(inviteCode);
            if(referredUser != null) {
                referredUser.getPoints().setGreen(referredUser.getPoints().getGreen() + 3);
                userService.updateUser(referredUser);
            }
        }

        userInfo.setPassword("");
        userInfo.setSessionID(RequestContextHolder.currentRequestAttributes().getSessionId());

        LOG.info("Registration validation passed with data {} and user saved.", userInfo);

        AppResponse response = new AppResponse();
        response.setData(userInfo);
        return response;
    }

    @RequestMapping(value = "/mobile/login", method = RequestMethod.POST)
    public AppResponse loginUserFromApp(@RequestBody UserInfo userInfo){
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

        AppResponse response = new AppResponse();
        response.setData(userInfo);
        return response;
    }

    @RequestMapping(value = "/mobile/scores", method = RequestMethod.GET)
    public AppResponse getScoresPage() {

        User user = userDataFacade.retrieveLoggedUser();

        if(user.getBanned()){
            throw new UserBannedException(getMessage("Banned.userForm.username"));
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("user", user);
        userData.put("fname", user.getFirstName());
        userData.put("username", user.getUsername());
        userData.put("greenPoints", user.getPoints().getGreen());
        userData.put("yellowPoints", user.getPoints().getYellow());
        userData.put("redPoints", user.getPoints().getRed());

        AppResponse response = new AppResponse();
        response.setData(userData);
        return response;
    }

    @RequestMapping(value="/mobile/logout", method = RequestMethod.GET)
    public AppResponse logoutPage(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new AppResponse();
    }

    private String getMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, null);
    }
}
