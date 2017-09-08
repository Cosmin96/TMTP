package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    private final UserDataFacade userDataFacade;

    public HomeController(final UserDataFacade userDataFacade) {
        this.userDataFacade = userDataFacade;
    }

    @RequestMapping("/scores")
    public String scoresPage(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userDataFacade.retrieveUser(username);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        return "livescores";
    }

    @RequestMapping("/profilePage")
    public String profilePage(){
        return "profile";
    }

    @RequestMapping("/shop")
    public String storePage(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        model.addAttribute("username", username);
        User user = userDataFacade.retrieveUser(username);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        return "store";
    }
}
