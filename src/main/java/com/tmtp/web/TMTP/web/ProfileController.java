package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.security.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserDataFacade userDataFacade;
    private final UserService userService;

    public ProfileController(final UserDataFacade userDataFacade,
                             final UserService userService) {
        this.userDataFacade = userDataFacade;
        this.userService = userService;
    }

    @RequestMapping("/profile/{username}")
    public String profilePage(@PathVariable("username") String username, Model model){

        User pageuser = userDataFacade.retrieveUser(username);
        User loggedInUser = userDataFacade.retrieveLoggedUser();

        if(pageuser.getId().equals(loggedInUser.getId())){
            model.addAttribute("settingsButton", true);
        }
        else{
            model.addAttribute("settingsButton", false);
        }
        getStadium(model, pageuser);
        model.addAttribute("fname", loggedInUser.getUsername());
        model.addAttribute("user", pageuser);
        model.addAttribute("greenPoints", pageuser.getPoints().getGreen());
        model.addAttribute("yellowPoints", pageuser.getPoints().getYellow());
        model.addAttribute("redPoints", pageuser.getPoints().getRed());
        return "profile";
    }

    @RequestMapping("/profile/{username}/stadiumUpgrade")
    public String upgradeStadium(@PathVariable("username") String username, Model model, RedirectAttributes redirectAttributes){
        User user = userDataFacade.retrieveUser(username);
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!user.getUsername().equals(loggedInUser.getUsername())){
            return "redirect:/home";
        }

        switch(user.getStadiumLevel()){
            case 1:
                if(user.getPoints().getGreen() < 100){
                    redirectAttributes.addFlashAttribute("error", true);
                    redirectAttributes.addFlashAttribute("errorMessage", "You do not have enough points to upgrade");
                    return "redirect:/profile/" + username;
                }
                user.getPoints().setGreen(user.getPoints().getGreen() - 100);
                break;
            case 2:
                if(user.getPoints().getGreen() < 150){
                    redirectAttributes.addFlashAttribute("error", true);
                    redirectAttributes.addFlashAttribute("errorMessage", "You do not have enough points to upgrade");
                    return "redirect:/profile/" + username;
                }
                user.getPoints().setGreen(user.getPoints().getGreen() - 150);
                break;
            case 3:
                if(user.getPoints().getGreen() < 200){
                    redirectAttributes.addFlashAttribute("error", true);
                    redirectAttributes.addFlashAttribute("errorMessage", "You do not have enough points to upgrade");
                    return "redirect:/profile/" + username;
                }
                user.getPoints().setGreen(user.getPoints().getGreen() - 200);
                break;
            case 4:
                if(user.getPoints().getGreen() < 250){
                    redirectAttributes.addFlashAttribute("error", true);
                    redirectAttributes.addFlashAttribute("errorMessage", "You do not have enough points to upgrade");
                    return "redirect:/profile/" + username;
                }
                user.getPoints().setGreen(user.getPoints().getGreen() - 200);
                redirectAttributes.addFlashAttribute("finalStadium", true);
                break;
        }
        user.setStadiumLevel(user.getStadiumLevel() + 1);
        userService.updateUser(user);

        return "redirect:/profile/" + username;
    }

    private Boolean checkUsersAreSame(String username){
        User pageUser = userDataFacade.retrieveUser(username);
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        return pageUser.equals(loggedInUser);
    }

    private void getStadium(Model model, User user){
        switch(user.getStadiumLevel()){
            case 1:
                model.addAttribute("upgradeButton", true);
                if(user.getPoints().getGreen() < 10){
                    model.addAttribute("enoughPoints", false);
                }
                else model.addAttribute("enoughPoints", true);
                model.addAttribute("buttonMessage", "Upgrade - 100 points");
                break;
            case 2:
                model.addAttribute("upgradeButton", true);
                if(user.getPoints().getGreen() < 50){
                    model.addAttribute("enoughPoints", false);
                }
                else model.addAttribute("enoughPoints", true);
                model.addAttribute("buttonMessage", "Upgrade - 150 points");
                break;
            case 3:
                model.addAttribute("upgradeButton", true);
                if(user.getPoints().getGreen() < 100){
                    model.addAttribute("enoughPoints", false);
                }
                else model.addAttribute("enoughPoints", true);
                model.addAttribute("buttonMessage", "Upgrade - 200 points");
                break;
            case 4:
                model.addAttribute("upgradeButton", true);
                if(user.getPoints().getGreen() < 250){
                    model.addAttribute("enoughPoints", false);
                }
                else model.addAttribute("enoughPoints", true);
                model.addAttribute("buttonMessage", "Upgrade - 250 points");
                break;
            case 5:
                model.addAttribute("upgradeButton", false);
                if(user.getPoints().getGreen() < 300){
                    model.addAttribute("enoughPoints", false);
                }
                else model.addAttribute("enoughPoints", true);
                model.addAttribute("buttonMessage", "Upgrade - 300 points");
                break;
        }
    }

}
