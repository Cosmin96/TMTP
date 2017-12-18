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

    @RequestMapping("/lobbies/{type}")
    public String scorePage(@PathVariable ("type") String type, Model model){
        User user = userDataFacade.retrieveLoggedUser();

        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("lobbyType", type);
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
            case "laliga":
                n = 3;
                break;
            case "brasil":
                n = 4;
                break;
            case "bundesliga":
                n = 5;
                break;
            case "primeiraliga":
                n = 6;
                break;
            case "serieA":
                n = 7;
                break;
            case "usamls":
                n = 8;
                break;
            case "chinese":
                n = 9;
                break;
            case "dutcheredivisie":
                n = 10;
                break;
            case "mexican":
                n = 11;
                break;
            case "belgium":
                n = 12;
                break;
            case "swiss":
                n = 13;
                break;
            case "russian":
                n = 14;
                break;
            case "ukranian":
                n = 15;
                break;
            case "turkish":
                n = 16;
                break;
            case "japan":
                n = 17;
                break;
            case "argentina":
                n = 18;
                break;
            case "scotland":
                n = 19;
                break;
            case "polish":
                n = 20;
                break;
            case "women":
                n = 21;
                break;
            case "barcelona":
                n = 22;
                break;
            case "realmadrid":
                n = 23;
                break;
            case "manu":
                n = 24;
                break;
            case "chelsea":
                n = 25;
                break;
            case "arsenal":
                n = 26;
                break;
            case "bayern":
                n = 27;
                break;
            case "liverpool":
                n = 28;
                break;
            case "psg":
                n = 29;
                break;
            case "juventus":
                n = 30;
                break;
            case "acmilan":
                n = 31;
                break;
            case "mancity":
                n = 32;
                break;
            case "tottenham":
                n = 33;
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
