package com.tmtp.web.TMTP.web.mobile;

import com.tmtp.web.TMTP.dto.AppResponse;
import com.tmtp.web.TMTP.dto.exceptions.BadFormatException;
import com.tmtp.web.TMTP.dto.exceptions.NoUserFound;
import com.tmtp.web.TMTP.dto.exceptions.UserBannedException;
import com.tmtp.web.TMTP.entity.*;
import com.tmtp.web.TMTP.security.SecurityService;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.StorageService;
import com.tmtp.web.TMTP.utils.RequestValidator;
import com.tmtp.web.TMTP.web.PrivateLobbyFacade;
import com.tmtp.web.TMTP.web.UserDataFacade;
import com.tmtp.web.TMTP.web.VideoPostsFacade;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

@RestController
public class IOSController {

    private static final Logger LOG = LoggerFactory.getLogger(IOSController.class);

    private final UserService userService;
    private final SecurityService securityService;
    private final RequestValidator requestValidator;
    private final UserDataFacade userDataFacade;
    private final PrivateLobbyFacade privateLobbyFacade;
    private final VideoPostsFacade videoPostsFacade;
    private final MessageSource messageSource;
    private final StorageService storageService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public IOSController(final UserService userService,
                         final SecurityService securityService,
                         final RequestValidator requestValidator,
                         final UserDataFacade userDataFacade,
                         final VideoPostsFacade videoPostsFacade,
                         final PrivateLobbyFacade privateLobbyFacade,
                         final MessageSource messageSource,
                         final StorageService storageService,
                         final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.securityService = securityService;
        this.requestValidator = requestValidator;
        this.userDataFacade = userDataFacade;
        this.videoPostsFacade = videoPostsFacade;
        this.privateLobbyFacade = privateLobbyFacade;
        this.messageSource = messageSource;
        this.storageService = storageService;
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

        userService.save(user);
        securityService.autologin(userInfo.getUsername(), userInfo.getPassword());

        if (userInfo.getImage() != null && !userInfo.getImage().isEmpty()) {
            String photoName = storageService.store(userInfo.getImage(), userInfo.getUsername());
            LOG.info("Username {}, photo saved: {}.", userInfo.getUsername(), photoName);
        }

        if (userInfo.getInviteCode() != null) {
            // Check if code belongs to any user
            User referredUser = userService.findById(userInfo.getInviteCode());
            if (referredUser != null) {
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
    public AppResponse loginUserFromApp(@RequestBody UserInfo userInfo) {
        User user = userDataFacade.retrieveUser(userInfo.getUsername());

        if (user == null || !bCryptPasswordEncoder.matches(userInfo.getPassword(), user.getPassword())) {
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

        if (user.getBanned()) {
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

    @RequestMapping(value = "/mobile/home", method = RequestMethod.GET)
    public AppResponse getHomeFeed() {

        User user = userDataFacade.retrieveLoggedUser();
        if (user.getBanned()) {
            throw new UserBannedException(getMessage("Banned.userForm.username"));
        }

        List<VideoPosts> posts = videoPostsFacade.retrieveListOfVideoPosts();

        //"algorithmic news feed"
        //Collections.shuffle(posts);

        Collections.sort(posts, new Comparator<VideoPosts>() {
            @Override
            public int compare(VideoPosts videoPosts, VideoPosts t1) {
                return videoPosts.getId().compareTo(t1.getId());
            }
        });
        Collections.reverse(posts);

        List<String> allUsers = new ArrayList<String>();
        for (User oneuser : userDataFacade.retrieveAllUsers()) {
            allUsers.add(oneuser.getUsername());
        }

        List<PrivateLobby> joinedLobbies = new ArrayList<PrivateLobby>();
        if (!privateLobbyFacade.retrieveAll().isEmpty()) {
            for (PrivateLobby lobby : privateLobbyFacade.retrieveAll()) {
                if (lobby.getJoinedUsers().contains(user.getUsername())) {
                    joinedLobbies.add(lobby);
                }
            }
        }

        Map<String, Object> userData = new HashMap<>();

        userData.put("allUsers", allUsers);
        userData.put("user", user);
        userData.put("fname", user.getFirstName());
        userData.put("username", user.getUsername());
        userData.put("greenPoints", user.getPoints().getGreen());
        userData.put("yellowPoints", user.getPoints().getYellow());
        userData.put("redPoints", user.getPoints().getRed());

        if (!joinedLobbies.isEmpty()) {
            userData.put("joinedLobbies", joinedLobbies);
            userData.put("myLobbies", true);
        } else {
            userData.put("myLobbies", false);
        }

        if (user.getPrivateLobby()) {
            PrivateLobby userLobby = privateLobbyFacade.findByCreator(user.getUsername());
            userData.put("myLobby", userLobby);
            userData.put("hasLobbies", true);
            userData.put("hasOwnLobby", true);
        } else {
            userData.put("hasOwnLobby", false);
            userData.put("hasLobbies", false);
        }
        userData.put("posts", posts);

        AppResponse response = new AppResponse();
        response.setData(userData);
        return response;
    }

    @RequestMapping(value = "/mobile/logout", method = RequestMethod.GET)
    public AppResponse logoutPage(HttpServletRequest request, HttpServletResponse response) {
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
