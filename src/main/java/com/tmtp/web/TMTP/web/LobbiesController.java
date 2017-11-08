package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LobbiesController {

    private final UserDataFacade userDataFacade;

    public LobbiesController(final UserDataFacade userDataFacade) {
        this.userDataFacade = userDataFacade;
    }

    @RequestMapping("/lobbies")
    public String scoresPage(Model model){
        User user = userDataFacade.retrieveLoggedUser();

        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());

        return "lobbies";
    }

    @RequestMapping("/lobby/{league}")
    public String lobbyPage(@PathVariable("league") String league, Model model){
        int n = 0;
        User user = userDataFacade.retrieveLoggedUser();
        switch(league){
            case "fapremier":
                n = 1;
                break;
            case "league1":
                n = 2;
                break;
            case "bundesliga":
                n = 3;
                break;
            case "serieA":
                n = 4;
                break;
            case "laliga":
                n = 5;
                break;
            case "primeiraliga":
                n = 6;
                break;
            case "dutcheredivisie":
                n = 7;
                break;

        }
        model.addAttribute("league", n);
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        return "league";
    }
}
