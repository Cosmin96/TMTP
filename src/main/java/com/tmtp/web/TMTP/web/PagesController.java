package com.tmtp.web.TMTP.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PagesController {

    @RequestMapping("/")
    public String homePage(){
        return "index.html";
    }

    @RequestMapping("/authentication")
    public String loginPage(){
        return "login.html";
    }

    @RequestMapping("/scores")
    public String scoresPage(){
        return "livescores.html";
    }

    @RequestMapping("/profilePage")
    public String profilePage(){
        return "profile.html";
    }

    @RequestMapping("/shop")
    public String storePage(){
        return "store.html";
    }
}
