package com.tmtp.web.TMTP.web.mobile;

import com.tmtp.web.TMTP.dto.AppResponse;
import com.tmtp.web.TMTP.dto.CloudinaryObject;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.cloud.CloudStorageService;
import com.tmtp.web.TMTP.web.UserDataFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class MobileUploadController {

    @Value("${cloudinary.profile.folder}")
    private String profileBucket;

    private final CloudStorageService cloudStorage;
    private final UserService userService;
    private final UserDataFacade userDataFacade;

    @Autowired
    public MobileUploadController(UserService userService,
                                CloudStorageService cloudStorage,
                                UserDataFacade userDataFacade) {
        this.userService = userService;
        this.cloudStorage = cloudStorage;
        this.userDataFacade = userDataFacade;
    }

    @RequestMapping(value = "/mobile/profileUpload", method = RequestMethod.POST)
    public AppResponse handleFileUpload(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) throws IOException {

        CloudinaryObject cloudinaryObject = cloudStorage.uploadFile(file, profileBucket);

        User loggedInUser = userDataFacade.getUserFromAuthHeader(authHeader);
        loggedInUser.setProfileImageUrl(cloudinaryObject.getSecureUrl());
        userService.updateUser(loggedInUser);

        AppResponse response = new AppResponse();
        response.setData(loggedInUser);
        return response;
    }
}
