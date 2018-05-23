package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.dto.CloudinaryObject;
import com.tmtp.web.TMTP.dto.enums.FileType;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.StorageService;
import com.tmtp.web.TMTP.service.cloud.CloudStorageService;
import com.tmtp.web.TMTP.service.cloud.CloudStorageServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class FileUploadController {

    private static final Logger LOG = LoggerFactory.getLogger(FileUploadController.class);

    @Value("${cloudinary.profile.folder}")
    private String profileBucket;

    @Value("${user.default.profileImage}")
    private String defaultProfilePicUrl;

    private final StorageService storageService;
    private final CloudStorageService cloudStorage;
    private final UserService userService;
    private final UserDataFacade userDataFacade;

    @Autowired
    public FileUploadController(StorageService storageService,
                                UserService userService,
                                CloudStorageService cloudStorage,
                                UserDataFacade userDataFacade) {
        this.storageService = storageService;
        this.userService = userService;
        this.cloudStorage = cloudStorage;
        this.userDataFacade = userDataFacade;
    }

    @RequestMapping(value = "/settings/{username}/profileUpload", method = RequestMethod.POST)
    public String handleFileUpload(@PathVariable("username") String username,
                                   @RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) throws IOException {

        CloudinaryObject cloudinaryObject = cloudStorage.uploadFile(file, FileType.IMAGE, profileBucket);

        User loggedInUser = userDataFacade.retrieveLoggedUser();
        loggedInUser.setProfileImageUrl(cloudinaryObject.getSecureUrl());
        userService.updateUser(loggedInUser);
        return "redirect:/settings/" + username;
    }

    @RequestMapping(value = "/admin/edit/{username}/profileUpload", method = RequestMethod.POST)
    public String handleFileUploadByAdmin(@PathVariable("username") String username,
                                          @RequestParam("file") MultipartFile file,
                                          RedirectAttributes redirectAttributes) throws IOException {

        CloudinaryObject cloudinaryObject = cloudStorage.uploadFile(file, FileType.IMAGE, profileBucket);

        User user = userDataFacade.retrieveLoggedUser();
        user.setProfileImageUrl(cloudinaryObject.getSecureUrl());
        userService.updateUser(user);
        return "redirect:/admin/edit/" + username;
    }

    @RequestMapping(value = "/getImage/{imageName}")
    @ResponseBody
    public byte[] getImage(@PathVariable("imageName") String imageName, HttpServletRequest request) {
        String rpath = "/img/profile";
        rpath = rpath + "/" + imageName; // whatever path you used for storing the file
        Path path = Paths.get(rpath);
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println("exception thrown");
        }
        return null;
    }

    @GetMapping("/getFiles/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping("/getJacket/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveJacketFile(@PathVariable String filename) throws IOException {

        Resource file = storageService.loadJacketAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(new InputStreamResource(file.getInputStream()));
    }

    @GetMapping("/getProfilePic/{username}")
    @ResponseBody
    public String getprofileImage(@PathVariable String username) {

        User user = userService.findByUsername(username);
        if (user == null) {
            return defaultProfilePicUrl;
        } else {
            return user.getProfileImageUrl();
        }
    }
}
