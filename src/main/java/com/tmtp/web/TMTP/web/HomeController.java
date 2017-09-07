package com.tmtp.web.TMTP.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {

    @RequestMapping("/scores")
    public String scoresPage(){
        return "livescores";
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
