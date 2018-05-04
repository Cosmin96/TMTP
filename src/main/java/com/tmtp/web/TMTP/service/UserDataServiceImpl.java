package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.PrivateLobby;
import com.tmtp.web.TMTP.entity.User;
import com.tmtp.web.TMTP.entity.VideoPosts;
import com.tmtp.web.TMTP.web.PrivateLobbyFacade;
import com.tmtp.web.TMTP.web.UserDataFacade;
import com.tmtp.web.TMTP.web.VideoPostsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDataServiceImpl implements UserDataService {

    private final UserDataFacade userDataFacade;
    private final PrivateLobbyFacade privateLobbyFacade;
    private final VideoPostsFacade videoPostsFacade;

    @Autowired
    public UserDataServiceImpl(
            UserDataFacade userDataFacade, PrivateLobbyFacade privateLobbyFacade, VideoPostsFacade videoPostsFacade) {
        this.userDataFacade = userDataFacade;
        this.privateLobbyFacade = privateLobbyFacade;
        this.videoPostsFacade = videoPostsFacade;
    }

    @Override
    public Map<String, Object> getUserScore(User user) {

        Map<String, Object> userData = new HashMap<>();
        userData.put("user", user);
        userData.put("fname", user.getFirstName());
        userData.put("username", user.getUsername());
        userData.put("greenPoints", user.getPoints().getGreen());
        userData.put("yellowPoints", user.getPoints().getYellow());
        userData.put("redPoints", user.getPoints().getRed());

        return userData;
    }

    @Override
    public Map<String, Object> getUserHomeFeed(User user) {

        List<VideoPosts> posts = videoPostsFacade.retrieveListOfVideoPosts();

        posts.sort(Comparator.comparing(VideoPosts::getId));
        Collections.reverse(posts);

        List<String> allUsers = new ArrayList<String>();
        for (User oneuser : userDataFacade.retrieveAllUsers()) {
            allUsers.add(oneuser.getUsername());
        }

        List<PrivateLobby> joinedLobbies = new ArrayList<PrivateLobby>();
        if (!privateLobbyFacade.retrieveAll().isEmpty()) {
            for (PrivateLobby lobby : privateLobbyFacade.retrieveAll()) {
                if (lobby.getJoinedUsers().contains(user.getUsername())) {
                    joinedLobbies.add(lobby);
                }
            }
        }

        Map<String, Object> userData = new HashMap<>();

        userData.put("allUsers", allUsers);
        userData.put("user", user);
        userData.put("fname", user.getFirstName());
        userData.put("username", user.getUsername());
        userData.put("greenPoints", user.getPoints().getGreen());
        userData.put("yellowPoints", user.getPoints().getYellow());
        userData.put("redPoints", user.getPoints().getRed());

        if (!joinedLobbies.isEmpty()) {
            userData.put("joinedLobbies", joinedLobbies);
            userData.put("myLobbies", true);
        } else {
            userData.put("myLobbies", false);
        }

        if (user.getPrivateLobby()) {
            PrivateLobby userLobby = privateLobbyFacade.findByCreator(user.getUsername());
            userData.put("myLobby", userLobby);
            userData.put("hasLobbies", true);
            userData.put("hasOwnLobby", true);
        } else {
            userData.put("hasOwnLobby", false);
            userData.put("hasLobbies", false);
        }
        userData.put("posts", posts);

        return userData;
    }
}
