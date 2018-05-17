package com.tmtp.web.TMTP.web.mobile;

import com.tmtp.web.TMTP.dto.AppResponse;
import com.tmtp.web.TMTP.dto.enums.DeviceType;
import com.tmtp.web.TMTP.dto.enums.TokenType;
import com.tmtp.web.TMTP.dto.exceptions.BadFormatException;
import com.tmtp.web.TMTP.dto.exceptions.NoUserFound;
import com.tmtp.web.TMTP.dto.exceptions.UserBannedException;
import com.tmtp.web.TMTP.entity.Team;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.UserInfo;
import com.tmtp.web.TMTP.entity.UserRegistration;
import com.tmtp.web.TMTP.security.SecurityService;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.StorageService;
import com.tmtp.web.TMTP.service.TokenInfoService;
import com.tmtp.web.TMTP.service.UserDataService;
import com.tmtp.web.TMTP.utils.RequestValidator;
import com.tmtp.web.TMTP.web.PrivateLobbyFacade;
import com.tmtp.web.TMTP.web.UserDataFacade;
import com.tmtp.web.TMTP.web.VideoPostsFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class IOSController {

    private static final Logger LOG = LoggerFactory.getLogger(IOSController.class);

    @Value("${user.default.profileImage}")
    private String defaultProfilePicUrl;

    private final UserService userService;
    private final SecurityService securityService;
    private final RequestValidator requestValidator;
    private final UserDataFacade userDataFacade;
    private final UserDataService userDataService;
    private final MessageSource messageSource;
    private final StorageService storageService;
    private final TokenInfoService tokenInfoService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public IOSController(final UserService userService,
                         final SecurityService securityService,
                         final RequestValidator requestValidator,
                         final UserDataFacade userDataFacade,
                         final UserDataService userDataService,
                         final MessageSource messageSource,
                         final TokenInfoService tokenInfoService,
                         final StorageService storageService,
                         final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.securityService = securityService;
        this.requestValidator = requestValidator;
        this.userDataFacade = userDataFacade;
        this.userDataService = userDataService;
        this.messageSource = messageSource;
        this.storageService = storageService;
        this.tokenInfoService = tokenInfoService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @RequestMapping(value = "/mobile/register", method = RequestMethod.POST)
    public AppResponse registerUserFromApp(
            @RequestBody UserRegistration userInfo) {

        String errorMessage = requestValidator.validateRegistration(userInfo);
        if (errorMessage != null) {
            throw new BadFormatException(errorMessage);
        }

        userInfo.setUsername(userInfo.getUsername().replaceAll(" ", ""));
        userInfo.setUsername(userInfo.getUsername().replaceAll("[^\\w\\s]+", ""));
        userInfo.setUsername(userInfo.getUsername().toLowerCase());

        User user = new User();
        user.setUsername(userInfo.getUsername());
        user.setPassword(userInfo.getPassword());
        user.setFirstName(userInfo.getFirstName());
        user.setLastName(userInfo.getLastName());
        user.setEmail(userInfo.getEmail());
        user.setProfile("no");
        user.setTeam(Team.ARSENAL);
        user.setProfileImageUrl(defaultProfilePicUrl);

        if (userInfo.getInviteCode() != null) {
            // Check if code belongs to any user
            User referredUser = userService.findById(userInfo.getInviteCode());
            if (referredUser != null) {
                referredUser.getPoints().setGreen(referredUser.getPoints().getGreen() + 3);
                userService.updateUser(referredUser);
            }
        }

        userService.save(user);
        securityService.autologin(userInfo.getUsername(), userInfo.getPassword());

        String tokenValue = RequestContextHolder.currentRequestAttributes().getSessionId();
        tokenInfoService.persistToken(userInfo.getUsername(), tokenValue, TokenType.ACCESS_TOKEN, DeviceType.IOS);

        userInfo.setPassword("");
        userInfo.setSessionID(tokenValue);

        LOG.info("Registration validation passed with data {} and user saved.", userInfo);

        AppResponse response = new AppResponse();
        response.setData(userInfo);
        return response;
    }

    @RequestMapping(value = "/mobile/login", method = RequestMethod.POST)
    public AppResponse loginUserFromApp(@RequestBody UserInfo userInfo) {
        User user = userDataFacade.retrieveUser(userInfo.getUsername());

        if (user == null || !bCryptPasswordEncoder.matches(userInfo.getPassword(), user.getPassword())) {
            throw new NoUserFound(getMessage("INVALID_CREDENTIALS"));
        }

        securityService.autologin(userInfo.getUsername(), userInfo.getPassword());

        String tokenValue = RequestContextHolder.currentRequestAttributes().getSessionId();
        tokenInfoService.persistToken(userInfo.getUsername(), tokenValue, TokenType.ACCESS_TOKEN, DeviceType.IOS);

        userInfo.setEmail(user.getEmail());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setPassword("");
        userInfo.setSessionID(tokenValue);

        AppResponse response = new AppResponse();
        response.setData(userInfo);
        return response;
    }

    @RequestMapping(value = "/mobile/scores", method = RequestMethod.GET)
    public AppResponse getScoresPage(@RequestHeader("Authorization") String authHeader) {

        User user = userDataFacade.getUserFromAuthHeader(authHeader);

        if (user.getBanned()) {
            throw new UserBannedException(getMessage("Banned.userForm.username"));
        }

        AppResponse response = new AppResponse();
        response.setData(userDataService.getUserScore(user));
        return response;
    }

    @RequestMapping(value = "/mobile/home", method = RequestMethod.GET)
    public AppResponse getHomeFeed(@RequestHeader("Authorization") String authHeader) {

        User user = userDataFacade.getUserFromAuthHeader(authHeader);

        if (user.getBanned()) {
            throw new UserBannedException(getMessage("Banned.userForm.username"));
        }

        AppResponse response = new AppResponse();
        response.setData(userDataService.getUserHomeFeed(user));
        return response;
    }

    @RequestMapping(value = "/mobile/logout", method = RequestMethod.GET)
    public AppResponse logoutPage(@RequestHeader("Authorization") String authHeder,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {

        //Clean access token for this user
        User user = userDataFacade.getUserFromAuthHeader(authHeder);

        String tokenValue = RequestContextHolder.currentRequestAttributes().getSessionId();
        tokenInfoService.deleteToken(user.getUsername(), tokenValue, TokenType.ACCESS_TOKEN, DeviceType.IOS);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return new AppResponse();
    }

    @RequestMapping(value = "/mobile/reset-password", method = RequestMethod.POST)
    public AppResponse resetPassword(@RequestBody UserInfo userInfo) {

        User user = userDataFacade.retrieveUser(userInfo.getUsername());

        AppResponse response = new AppResponse();

        if (user == null || !user.getEmail().equals(userInfo.getEmail())) {
            response.setSuccess(false);
            response.setData(getMessage("INVALID_USER_DATA"));
        } else if (userInfo.getPassword() == null || userInfo.getPassword().trim().isEmpty() ||
                userInfo.getPassword().length() < 6 || userInfo.getPassword().length() > 32) {
            response.setSuccess(false);
            response.setData(getMessage("Size.userForm.password"));
        } else if (user.getBanned()) {
            response.setSuccess(false);
            response.setData(getMessage("Banned.userForm.username"));
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(userInfo.getPassword()));
            userDataFacade.updateUser(user);
            response.setData(getMessage("PASSWORD_UPDATED"));
        }

        return response;
    }

    private String getMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, null);
    }
}
