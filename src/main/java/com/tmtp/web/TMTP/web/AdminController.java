package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.Comment;
import com.tmtp.web.TMTP.entity.PlayerKit;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.security.UserService;
import com.tmtp.web.TMTP.service.VideoPostsService;
import com.tmtp.web.TMTP.web.formobjects.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class AdminController {

    private final UserDataFacade userDataFacade;
    private final VideoPostsFacade videoPostsFacade;
    private final VideoPostsService videoPostsService;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminController(final UserDataFacade userDataFacade,
                           final VideoPostsFacade videoPostsFacade,
                           final VideoPostsService videoPostsService,
                           final UserService userService,
                           final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDataFacade = userDataFacade;
        this.videoPostsFacade = videoPostsFacade;
        this.videoPostsService = videoPostsService;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @RequestMapping("/post/{id}/delete")
    public String deletePost(@PathVariable("id") String id, Model model){

        User user = userDataFacade.retrieveLoggedUser();
        if(user.getAdmin().equals(false)){
            return "redirect:/home";
        }
        VideoPosts videoPosts = videoPostsFacade.retrievePostById(id);
        videoPostsService.deletePost(videoPosts);

        return "redirect:/home";
    }

    @RequestMapping("/post/{id}/{commentId}/delete")
    public String deletePostComment(@PathVariable("id") String id,
                                    @PathVariable("commentId") String commentId,
                                    Model model){

        User user = userDataFacade.retrieveLoggedUser();
        if(user.getAdmin().equals(false)){
            return "redirect:/home";
        }
        VideoPosts videoPosts = videoPostsFacade.retrievePostById(id);

        for (Comment comment : videoPosts.getComments()){
            if(comment.getId().equals(commentId)){
                videoPosts.getComments().remove(comment);
            }
            if(videoPosts.getComments().isEmpty()){
                break;
            }
        }
        videoPostsService.deletePostComment(videoPosts);
        return "redirect:/post/" + videoPosts.getId();
    }

    @RequestMapping("/admin")
    public String getAdminPage(Model model){
        User user = userDataFacade.retrieveLoggedUser();
        if(!user.getAdmin()){
            return "redirect:/home";
        }
        List<User> users = userDataFacade.retrieveAllUsers();
        model.addAttribute("user", user);
        model.addAttribute("users", users);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        return "adminpanel";
    }

    @RequestMapping("/admin/delete/{username}")
    public String deleteUser(@PathVariable("username") String username){
        User user = userDataFacade.retrieveLoggedUser();
        if(!user.getAdmin()){
            return "redirect:/home";
        }
        User userToDelete = userDataFacade.retrieveUser(username);
        userDataFacade.deleteUser(userToDelete);
        return "redirect:/admin";
    }

    @RequestMapping("/admin/edit/{username}")
    public String editUser(@PathVariable("username") String username, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);
        model.addAttribute("loggeduser", loggedInUser);
        model.addAttribute("user", user);
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        model.addAttribute("nameForm", new NameForm());
        model.addAttribute("passForm", new PassForm());
        model.addAttribute("overlayForm", new OverlayForm());
        model.addAttribute("kitForm", new PlayerKitForm());
        model.addAttribute("pointsForm", new PointsForm());
        model.addAttribute("bannedForm", new BannedForm());
        return "adminpaneledit";
    }

    @RequestMapping(value = "/admin/edit/{username}/updateName", method = RequestMethod.POST)
    public String updateNameByAdmin(@PathVariable("username") String username, @ModelAttribute("nameForm") NameForm nameForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);
        user.setFirstName(nameForm.getFname());
        user.setLastName(nameForm.getLname());
        userService.updateUser(user);

        return "redirect:/admin/edit/" + user.getUsername();
    }

    @RequestMapping(value = "/admin/edit/{username}/updatePassword", method = RequestMethod.POST)
    public String updatePasswordByAdmin(@PathVariable("username") String username, @ModelAttribute("passForm") PassForm passForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);
        if(passForm.getOldPass().equals(passForm.getNewPass())){
            user.setPassword(bCryptPasswordEncoder.encode(passForm.getNewPass()));
            userService.updateUser(user);
        }

        return "redirect:/admin/edit/" + user.getUsername();
    }

    @RequestMapping(value = "/admin/edit/{username}/updatePoints", method = RequestMethod.POST)
    public String updatePointsByAdmin(@PathVariable("username") String username, @ModelAttribute("pointsForm") PointsForm pointsForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);
        switch (pointsForm.getColor()){
            case "green":
                user.getPoints().setGreen(pointsForm.getPoints());
                break;
            case "yellow":
                user.getPoints().setYellow(pointsForm.getPoints());
                break;
            case "red":
                user.getPoints().setRed(pointsForm.getPoints());
                break;
            default:
                break;
        }
        userService.updateUser(user);
        return "redirect:/admin/edit/" + user.getUsername();
    }

    @RequestMapping(value = "/admin/edit/{username}/updateBan", method = RequestMethod.POST)
    public String updatePointsByAdmin(@PathVariable("username") String username, @ModelAttribute("bannedForm") BannedForm bannedForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);
        user.setBanned(bannedForm.getBanned());
        userService.updateUser(user);
        return "redirect:/admin/edit/" + user.getUsername();
    }

    @RequestMapping(value = "/admin/edit/{username}/updateOverlay", method = RequestMethod.POST)
    public String updateOverlayByAdmin(@PathVariable("username") String username, @ModelAttribute("overlayForm") OverlayForm overlayForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);

        user.setOverlay(overlayForm.getOverlayName());
        userService.updateUser(user);
        return "redirect:/admin/edit/" + user.getUsername();
    }

    @RequestMapping(value = "/admin/edit/{username}/updateKit", method = RequestMethod.POST)
    public String updatePlayerKitByAdmin(@PathVariable("username") String username, @ModelAttribute("kitForm") PlayerKitForm playerKitForm, Model model){
        User loggedInUser = userDataFacade.retrieveLoggedUser();
        if(!loggedInUser.getAdmin()){
            return "redirect:/home";
        }
        User user = userDataFacade.retrieveUser(username);
        user.setPlayerKit(new PlayerKit(playerKitForm.getJacket(), playerKitForm.getShorts(), playerKitForm.getSocks(), playerKitForm.getFootball()));
        userService.updateUser(user);

        return "redirect:/admin/edit/" + user.getUsername();
    }

}
