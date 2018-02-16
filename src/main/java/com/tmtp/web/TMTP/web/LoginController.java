package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.PrivateLobby;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.repository.ShopItemRepository;
import com.tmtp.web.TMTP.security.SecurityService;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.security.UserValidator;
import com.tmtp.web.TMTP.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Controller
public class LoginController {

    private final UserService userService;
    private final SecurityService securityService;
    private final UserValidator userValidator;
    private final UserDataFacade userDataFacade;
    private final VideoPostsFacade videoPostsFacade;
    private final MessageSource messageSource;
    private final PrivateLobbyFacade privateLobbyFacade;
    private final StorageService storageService;
    @Autowired
    private ShopItemRepository shopItemRepository;

    public LoginController(final UserService userService,
                           final SecurityService securityService,
                           final UserValidator userValidator,
                           final UserDataFacade userDataFacade,
                           final VideoPostsFacade videoPostsFacade,
                           final MessageSource messageSource,
                           final PrivateLobbyFacade privateLobbyFacade,
                           final StorageService storageService) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
        this.userDataFacade = userDataFacade;
        this.videoPostsFacade = videoPostsFacade;
        this.messageSource = messageSource;
        this.privateLobbyFacade = privateLobbyFacade;
        this.storageService = storageService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String registration(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("userForm", new User());
        return "login";
    }

    @RequestMapping(value = "/registerUser", method = RequestMethod.POST)
    public String registration(@ModelAttribute("user") User userForm,
                               BindingResult bindingResult,
                               Model model,
                               RedirectAttributes redirectAttributes,
                               @RequestParam("file") MultipartFile file) {

        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage(bindingResult.getFieldError(), null));
            return "redirect:/register";
        }
        userForm.setUsername(userForm.getUsername().replaceAll(" ",""));
        if(!file.isEmpty()) {
            String photoName = storageService.store(file, userForm.getUsername());
            userForm.setProfile("yes");
        }
        else userForm.setProfile("no");

        userService.save(userForm);
        securityService.autologin(userForm.getUsername(), userForm.getPassword());

        // Set flag to display Amazon popup after registration
        redirectAttributes.addFlashAttribute("popup", "amazon");

        return "redirect:/home";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@ModelAttribute("userForm") User userForm, Model model, RedirectAttributes redirectAttributes){
        User user = userDataFacade.retrieveUser(userForm.getUsername());
        if(user.getBanned()){
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("errorMessage", "You are currently banned for unsuitable behaviour! Please wait for an admin to remove your restrictions");
            return "redirect:/register";
        }
        securityService.autologin(userForm.getUsername(), userForm.getPassword());

        // Set flag to display Amazon popup after each login
        redirectAttributes.addFlashAttribute("popup", "amazon");

        return "redirect:/home";
    }

    @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
    public String home(Model model) {

        User user = userDataFacade.retrieveLoggedUser();
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
        for(User oneuser : userDataFacade.retrieveAllUsers()){
            allUsers.add(oneuser.getUsername());
        }

        List<PrivateLobby> joinedLobbies = new ArrayList<PrivateLobby>();
        if(!privateLobbyFacade.retrieveAll().isEmpty()){
            for(PrivateLobby lobby : privateLobbyFacade.retrieveAll()){
                if(lobby.getJoinedUsers().contains(user.getUsername())){
                    joinedLobbies.add(lobby);
                }
            }
        }
        if(!joinedLobbies.isEmpty()) {
            model.addAttribute("joinedLobbies", joinedLobbies);
            model.addAttribute("myLobbies", true);
        }
        else{
            model.addAttribute("myLobbies", false);
        }

        if(user.getPrivateLobby()) {
            PrivateLobby userLobby = privateLobbyFacade.findByCreator(user.getUsername());
            model.addAttribute("myLobby", userLobby);
            model.addAttribute("hasLobbies", true);
            model.addAttribute("hasOwnLobby", true);
        }
        else{
            model.addAttribute("hasOwnLobby", false);
            model.addAttribute("hasLobbies", false);
        }
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("videoPostForm", new VideoPosts());
        model.addAttribute("posts", posts);

        return "index";
    }

    @RequestMapping(value = "/scores")
    public String getScoresPage(Model model) {

        User user = userDataFacade.retrieveLoggedUser();

        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());

        return "scores";
    }

    @RequestMapping(value = "/news")
    public String getNewsPage(Model model) {

        User user = userDataFacade.retrieveLoggedUser();

        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());

        return "news";
    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/register";
    }
}
