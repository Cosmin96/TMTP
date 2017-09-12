package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.Comment;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.repository.VideoPostsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class VideoPostsServiceImpl implements VideoPostsService {

    private final VideoPostsRepository videoPostsRepository;

    public VideoPostsServiceImpl(final VideoPostsRepository videoPostsRepository) {
        this.videoPostsRepository = videoPostsRepository;
    }

    @Override
    public List<VideoPosts> retrieveListOfVideoPosts(){
        return videoPostsRepository.findAll();
    }

    @Override
    public List<VideoPosts> retrieveListOfVideoPostsByUsername(String username){
        return videoPostsRepository.findByCreator(username);
    }

    @Override
    public VideoPosts retrieveVideoPostById(String id){
        return videoPostsRepository.findById(id);
    }

    @Override
    public void createVideoPost(VideoPosts videoPosts, User user){

        String url = "https://www.youtube.com/embed/" + videoPosts.getLink().replace("https://www.youtube.com/watch?v=", "");
        videoPosts.setComments(Collections.emptyList());
        videoPosts.setCreator(user.getUsername());
        videoPosts.setUser(user);
        videoPosts.setLink(url);

        videoPostsRepository.save(videoPosts);
    }

    @Override
    public void addNewComment(VideoPosts videoPosts, Comment comment, User user){

        comment.setTimestamp(LocalDateTime.now());
        comment.setUser(user);
        videoPosts.getComments().add(comment);

        videoPostsRepository.save(videoPosts);
    }
}
