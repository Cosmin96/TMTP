package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.repository.VideoPostsRepository;
import org.springframework.stereotype.Service;

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
        videoPosts.setCreator(user.getUsername());
        videoPosts.setUser(user);
        videoPosts.setLink(url);
        videoPostsRepository.save(videoPosts);
    }
}
