package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.Comment;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.service.VideoPostsService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VideoPostsFacade {

    private final VideoPostsService videoPostsService;

    public VideoPostsFacade(final VideoPostsService videoPostsService) {
        this.videoPostsService = videoPostsService;
    }

    public List<VideoPosts> retrieveListOfVideoPosts(){
        return videoPostsService.retrieveListOfVideoPosts();
    }

    public List<VideoPosts> retrieveListOfVideoPostsByUsername(String username){
        return videoPostsService.retrieveListOfVideoPostsByUsername(username);
    }

    public VideoPosts retrievePostById(String id){
        return videoPostsService.retrieveVideoPostById(id);
    }

    public void createVideoPost(VideoPosts videoPosts, User user){
        videoPostsService.createVideoPost(videoPosts, user);
    }

    public void addNewComment(VideoPosts videoPosts, Comment comment, User user){
        videoPostsService.addNewComment(videoPosts, comment, user);
    }

}
