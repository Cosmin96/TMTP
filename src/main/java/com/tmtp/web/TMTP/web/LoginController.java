package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.security.SecurityService;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.security.UserValidator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    private final UserService userService;
    private final SecurityService securityService;
    private final UserValidator userValidator;
    private final UserDataFacade userDataFacade;

    public LoginController(final UserService userService,
                           final SecurityService securityService,
                           final UserValidator userValidator,
                           final UserDataFacade userDataFacade) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
        this.userDataFacade = userDataFacade;
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
                               Model model) {
        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "login";
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userDataFacade.retrieveUser(username);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        return "index";
    }

}
