package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.User;
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
        User user = userDataFacade.retrieveLoggedUser();

        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());

        return "livescores";
    }

    @RequestMapping("/shop")
    public String storePage(Model model){
        User user = userDataFacade.retrieveLoggedUser();

        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());

        return "store";
    }
}
