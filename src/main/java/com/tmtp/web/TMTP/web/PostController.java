package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PostController {

    private final UserDataFacade userDataFacade;
    private final VideoPostsFacade videoPostsFacade;

    public PostController(final UserDataFacade userDataFacade,
                          final VideoPostsFacade videoPostsFacade) {
        this.userDataFacade = userDataFacade;
        this.videoPostsFacade = videoPostsFacade;
    }

    @RequestMapping("/newPost")
    public String createNewPost(@ModelAttribute("videoPostForm") VideoPosts videoPosts, Model model){
        User user = userDataFacade.retrieveLoggedUser();
        videoPostsFacade.createVideoPost(videoPosts, user);
        return "redirect:/home";
    }

    @RequestMapping("/post/{id}")
    public String profilePage(@PathVariable("id") String id, Model model){
        User user = userDataFacade.retrieveLoggedUser();
        VideoPosts videoPosts = videoPostsFacade.retrievePostById(id);
        model.addAttribute("post", videoPosts);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());
        return "post";
    }
}
