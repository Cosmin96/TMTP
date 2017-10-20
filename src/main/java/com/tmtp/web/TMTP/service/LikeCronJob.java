package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.web.UserDataFacade;
import com.tmtp.web.TMTP.web.VideoPostsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LikeCronJob {

    @Autowired
    private VideoPostsFacade videoPostsFacade;
    @Autowired
    private UserDataFacade userDataFacade;

    @Scheduled(fixedRate = 3600000)
    public void grantPoints(){
        List<VideoPosts> videoPosts = videoPostsFacade.retrieveListOfVideoPosts();
        for(VideoPosts post : videoPosts){
            if(!post.getGrantPoint()){
                if((post.getLikes() - post.getDislikes()) >= 100){
                    post.setGrantPoint(true);
                    videoPostsFacade.updateVideoPost(post);
                    User user = userDataFacade.retrieveUser(post.getCreator());
                    user.getPoints().setGreen(user.getPoints().getGreen() + 1);
                    userDataFacade.updateUser(user);
                }
            }
        }
    }

}
