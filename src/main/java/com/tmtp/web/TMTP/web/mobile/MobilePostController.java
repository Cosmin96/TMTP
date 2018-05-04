package com.tmtp.web.TMTP.web.mobile;

import com.tmtp.web.TMTP.dto.AppResponse;
import com.tmtp.web.TMTP.dto.exceptions.BadFormatException;
import com.tmtp.web.TMTP.dto.exceptions.UserBannedException;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.UserDataService;
import com.tmtp.web.TMTP.web.UserDataFacade;
import com.tmtp.web.TMTP.web.VideoPostsFacade;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
public class MobilePostController {

    private static final Logger LOG = LoggerFactory.getLogger(IOSController.class);

    private final UserDataFacade userDataFacade;
    private final UserService userService;
    private final UserDataService userDataService;
    private final VideoPostsFacade videoPostsFacade;
    private final MessageSource messageSource;

    @Autowired
    public MobilePostController(final UserDataFacade userDataFacade,
                                final UserService userService,
                                final UserDataService userDataService,
                                final MessageSource messageSource,
                                final VideoPostsFacade videoPostsFacade) {
        this.userDataFacade = userDataFacade;
        this.userService = userService;
        this.userDataService = userDataService;
        this.videoPostsFacade = videoPostsFacade;
        this.messageSource = messageSource;
    }

    @RequestMapping(value = "/mobile/post", method = RequestMethod.POST)
    public AppResponse createNewPost(@RequestBody VideoPosts videoPosts) {

        User user = userDataFacade.retrieveLoggedUser();
        AppResponse response = new AppResponse();

        if (user.getBanned()) {
            throw new UserBannedException(getMessage("Banned.userForm.username"));
        }

        if (videoPosts.getDescription().isEmpty()) {
            throw new BadFormatException("Your post does not contain any text or description");
        }

        if (videoPostsFacade.filterComment(videoPosts.getDescription())) {
            user.getPoints().setYellow(user.getPoints().getYellow() + 1);
            if (user.getPoints().getYellow() == 2) {
                user.getPoints().setYellow(0);
                user.getPoints().setRed(user.getPoints().getRed() + 1);
                user.setBanned(true);
                user.setBanTime(DateTime.now());
                userService.updateUser(user);
                response.setData(userDataService.getUserScore(user));
                return response;
            }
            userService.updateUser(user);
            response.setSuccess(false);
            response.setDetail(getMessage("BAN_RACIST_LANGUAGE"));
            response.setData(userDataService.getUserHomeFeed(user));
            return response;
        }
        videoPostsFacade.createVideoPost(videoPosts, userDataFacade.retrieveLoggedUser());
        return response;
    }

    @RequestMapping(value = "/mobile/post/{id}", method = RequestMethod.PUT)
    public AppResponse updateFlagStatusOfPost(@PathVariable("id") String id,
                                              @RequestParam boolean flagPost) {

        User user = userDataFacade.retrieveLoggedUser();
        if (user.getBanned()) {
            throw new UserBannedException(getMessage("Banned.userForm.username"));
        }

        AppResponse response = new AppResponse();

        VideoPosts videoPosts = videoPostsFacade.retrievePostById(id);
        videoPosts.setFlagged(flagPost);
        videoPostsFacade.updateVideoPost(videoPosts);

        response.setData(userDataService.getUserHomeFeed(user));
        return response;
    }


    private String getMessage(String messageKey) {
        return messageSource.getMessage(messageKey, null, null);
    }
}
