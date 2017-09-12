package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;

import java.util.List;

public interface VideoPostsService {

    List<VideoPosts> retrieveListOfVideoPosts();

    List<VideoPosts> retrieveListOfVideoPostsByUsername(String username);

    VideoPosts retrieveVideoPostById(String id);

    void createVideoPost(VideoPosts videoPosts, User user);
}
