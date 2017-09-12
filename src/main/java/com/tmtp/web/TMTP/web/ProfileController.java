package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProfileController {

    private final UserDataFacade userDataFacade;

    public ProfileController(UserDataFacade userDataFacade) {
        this.userDataFacade = userDataFacade;
    }

    @RequestMapping("/profile/{username}")
    public String profilePage(@PathVariable("username") String username, Model model){
        User pageuser = userDataFacade.retrieveUser(username);
        User loggedInUser = userDataFacade.retrieveLoggedUser();

        model.addAttribute("loggedfname", loggedInUser.getFirstName());
        model.addAttribute("user", pageuser);
        model.addAttribute("greenPoints", pageuser.getPoints().getGreen());
        model.addAttribute("yellowPoints", pageuser.getPoints().getYellow());
        model.addAttribute("redPoints", pageuser.getPoints().getRed());
        return "profile";
    }
}
