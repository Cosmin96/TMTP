package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.Comment;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.security.UserService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PostController {

    private final UserDataFacade userDataFacade;
    private final UserService userService;
    private final VideoPostsFacade videoPostsFacade;

    public PostController(final UserDataFacade userDataFacade,
                          final UserService userService,
                          final VideoPostsFacade videoPostsFacade) {
        this.userDataFacade = userDataFacade;
        this.userService = userService;
        this.videoPostsFacade = videoPostsFacade;
    }

    @RequestMapping(value = "/newPost", method = RequestMethod.POST)
    public String createNewPost(@ModelAttribute("videoPostForm") VideoPosts videoPosts, Model model, RedirectAttributes redirectAttributes){

        if(videoPostsFacade.filterComment(videoPosts.getDescription())){
            User user = userDataFacade.retrieveLoggedUser();
            user.getPoints().setYellow(user.getPoints().getYellow() + 1);
            if(user.getPoints().getYellow() == 2){
                user.getPoints().setYellow(0);
                user.getPoints().setRed(user.getPoints().getRed() + 1);
                user.setBanned(true);
                user.setBanTime(DateTime.now());
            }
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("errorMessage", "You have used abusive language and we have penalized you as appropriate");
            return "redirect:/home";
        }
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
        model.addAttribute("comments", videoPosts.getComments());
        model.addAttribute("user", user);
        model.addAttribute("fname", user.getFirstName());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("greenPoints", user.getPoints().getGreen());
        model.addAttribute("yellowPoints", user.getPoints().getYellow());
        model.addAttribute("redPoints", user.getPoints().getRed());

        return "post";
    }

    @RequestMapping(value = "/newComment/{id}", method = RequestMethod.POST)
    public String postComment(@ModelAttribute("commentForm") Comment comment,
                              @PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes){
        VideoPosts videoPosts = videoPostsFacade.retrievePostById(id);
        if(videoPostsFacade.filterComment(comment.getComment())){
            User user = userDataFacade.retrieveLoggedUser();
            user.getPoints().setYellow(user.getPoints().getYellow() + 1);
            if(user.getPoints().getYellow() == 2){
                user.getPoints().setYellow(0);
                user.getPoints().setRed(user.getPoints().getRed() + 1);
                user.setBanTime(DateTime.now());
                user.setBanned(true);
            }
            userService.updateUser(user);
            redirectAttributes.addFlashAttribute("error", true);
            redirectAttributes.addFlashAttribute("errorMessage", "You have used abusive language and we have penalized you as appropriate");
            return "redirect:/post/" + id;
        }
        videoPostsFacade.addNewComment(videoPosts, comment, userDataFacade.retrieveLoggedUser());

        return "redirect:/post/" + id;
    }
}
