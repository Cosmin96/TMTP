package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.security.SecurityService;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.security.UserValidator;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

@Controller
public class LoginController {

    private final UserService userService;
    private final SecurityService securityService;
    private final UserValidator userValidator;
    private final UserDataFacade userDataFacade;
    private final VideoPostsFacade videoPostsFacade;
    private final MessageSource messageSource;

    public LoginController(final UserService userService,
                           final SecurityService securityService,
                           final UserValidator userValidator,
                           final UserDataFacade userDataFacade,
                           final VideoPostsFacade videoPostsFacade,
                           final MessageSource messageSource) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
        this.userDataFacade = userDataFacade;
        this.videoPostsFacade = videoPostsFacade;
        this.messageSource = messageSource;
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
                               Model model, RedirectAttributes redirectAttributes) {

        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage(bindingResult.getFieldError(), null));
            return "redirect:/register";
        }
        userService.save(userForm);

        securityService.autologin(userForm.getUsername(), userForm.getPassword());
        return "redirect:/home";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@ModelAttribute("userForm") User userForm, Model model){
        securityService.autologin(userForm.getUsername(), userForm.getPassword());
        return "redirect:/home";
    }

    @RequestMapping(value = {"/", "/home"}, method = RequestMethod.GET)
    public String home(Model model) {

        User user = userDataFacade.retrieveLoggedUser();
        List<VideoPosts> posts = videoPostsFacade.retrieveListOfVideoPosts();
        Collections.shuffle(posts);

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

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/register";
    }
}
