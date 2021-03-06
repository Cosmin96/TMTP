package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.PlayerKit;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.web.formobjects.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class ProfileSettingsController {

    private final UserDataFacade userDataFacade;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public ProfileSettingsController(final UserDataFacade userDataFacade,
                                     final UserService userService,
                                     final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDataFacade = userDataFacade;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @RequestMapping("/settings/{username}")
    public String profilePage(@PathVariable("username") String username, Model model){

        User pageUser = userDataFacade.retrieveUser(username);
        User loggedInUser = userDataFacade.retrieveLoggedUser();

        if(pageUser.getId().equals(loggedInUser.getId())){

            model.addAttribute("fname", loggedInUser.getFirstName());
            model.addAttribute("user", pageUser);
            model.addAttribute("inventory", loggedInUser.getShopItems());
            model.addAttribute("greenPoints", pageUser.getPoints().getGreen());
            model.addAttribute("yellowPoints", pageUser.getPoints().getYellow());
            model.addAttribute("redPoints", pageUser.getPoints().getRed());
            model.addAttribute("nameForm", new NameForm());
            model.addAttribute("passForm", new PassForm());
            model.addAttribute("overlayForm", new OverlayForm());
            model.addAttribute("kitForm", new PlayerKitForm());
            model.addAttribute("titleForm", new TitleForm());
            model.addAttribute("trophyForm", new TrophyForm());

            return "settings";
        }
        else{
            return "redirect:/home";
        }
    }

    @RequestMapping(value = "/settings/{username}/updateName", method = RequestMethod.POST)
    public String updateFirstandLastNames(@PathVariable("username") String username, @ModelAttribute("nameForm") NameForm nameForm, Model model){
        Boolean isUserRight = checkUsersAreSame(username);

        if(!isUserRight){
            return "redirect:/home";
        }

        User loggedInUser = userDataFacade.retrieveLoggedUser();

        loggedInUser.setFirstName(nameForm.getFname());
        loggedInUser.setLastName(nameForm.getLname());
        userService.updateUser(loggedInUser);

        return "redirect:/settings/" + loggedInUser.getUsername();
    }

    @RequestMapping(value = "/settings/{username}/updatePassword", method = RequestMethod.POST)
    public String updatePassword(@PathVariable("username") String username, @ModelAttribute("passForm") PassForm passForm, Model model){
        Boolean isUserRight = checkUsersAreSame(username);
        User pageUser = userDataFacade.retrieveUser(username);
        if(!isUserRight){
            return "redirect:/home";
        }

        if(passForm.getOldPass().isEmpty() || passForm.getNewPass().isEmpty()){
            return "redirect:/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
        }

        if(passForm.getOldPass().equals(passForm.getNewPass())){
            pageUser.setPassword(bCryptPasswordEncoder.encode(passForm.getNewPass()));
            userService.updateUser(pageUser);
        }

        return "redirect:/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
    }

    @RequestMapping(value = "/settings/{username}/updateOverlay", method = RequestMethod.POST)
    public String updateOverlay(@PathVariable("username") String username, @ModelAttribute("overlayForm") OverlayForm overlayForm, Model model){
        Boolean isUserRight = checkUsersAreSame(username);
        User pageUser = userDataFacade.retrieveUser(username);
        if(!isUserRight){
            return "redirect:/home";
        }

        pageUser.setOverlay(overlayForm.getOverlayName());
        userService.updateUser(pageUser);


        return "redirect:/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
    }

    @RequestMapping(value = "/settings/{username}/updateTitle", method = RequestMethod.POST)
    public String updateTitle(@PathVariable("username") String username, @ModelAttribute("titleForm") TitleForm titleForm, Model model){
        Boolean isUserRight = checkUsersAreSame(username);
        User pageUser = userDataFacade.retrieveUser(username);
        if(!isUserRight){
            return "redirect:/home";
        }

        pageUser.setTitle(titleForm.getTitleName());
        userService.updateUser(pageUser);

        return "redirect:/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
    }

    @RequestMapping(value = "/settings/{username}/updateTrophy", method = RequestMethod.POST)
    public String updateTrophy(@PathVariable("username") String username, @ModelAttribute("trophyForm") TrophyForm trophyForm, Model model){
        Boolean isUserRight = checkUsersAreSame(username);
        User pageUser = userDataFacade.retrieveUser(username);
        if(!isUserRight){
            return "redirect:/home";
        }

        pageUser.setTrophy(trophyForm.getName());
        userService.updateUser(pageUser);

        return "redirect:/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
    }

    @RequestMapping(value = "/settings/{username}/updateKit", method = RequestMethod.POST)
    public String updatePlayerKit(@PathVariable("username") String username, @ModelAttribute("kitForm") PlayerKitForm playerKitForm, Model model){

        Boolean isUserRight = checkUsersAreSame(username);
        User pageUser = userDataFacade.retrieveUser(username);
        if(!isUserRight){
            return "redirect:/home";
        }

        pageUser.setPlayerKit(new PlayerKit(playerKitForm.getJacket(), playerKitForm.getShorts(), playerKitForm.getSocks(), playerKitForm.getFootball()));
        userService.updateUser(pageUser);

        return "redirect:/settings/" + userDataFacade.retrieveLoggedUser().getUsername();
    }


    private Boolean checkUsersAreSame(String username){
        User pageUser = userDataFacade.retrieveUser(username);
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        return pageUser.getId().equals(loggedInUser.getId());
    }
}
