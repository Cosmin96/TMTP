package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.Comment;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PostController {

    private final UserDataFacade userDataFacade;
    private final VideoPostsFacade videoPostsFacade;

    public PostController(final UserDataFacade userDataFacade,
                          final VideoPostsFacade videoPostsFacade) {
        this.userDataFacade = userDataFacade;
        this.videoPostsFacade = videoPostsFacade;
    }

    @RequestMapping(value = "/newPost", method = RequestMethod.POST)
    public String createNewPost(@ModelAttribute("videoPostForm") VideoPosts videoPosts, Model model){
        videoPostsFacade.createVideoPost(videoPosts, userDataFacade.retrieveLoggedUser());
        return "redirect:/home";
    }

    @RequestMapping("/post/{id}")
    public String postPage(@PathVariable("id") String id, Model model){

        User user = userDataFacade.retrieveLoggedUser();
        VideoPosts videoPosts = videoPostsFacade.retrievePostById(id);

        model.addAttribute("post", videoPosts);
        model.addAttribute("id", id);
        model.addAttribute("commentForm", new Comment());
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());

        return "post";
    }

    @RequestMapping(value = "/newComment/{id}", method = RequestMethod.POST)
    public String postComment(@ModelAttribute("commentForm") Comment comment,
                              @PathVariable("id") String id, Model model){

        VideoPosts videoPosts = videoPostsFacade.retrievePostById(id);
        videoPostsFacade.addNewComment(videoPosts, comment, userDataFacade.retrieveLoggedUser());

        return "redirect:/post/" + id;
    }
}
