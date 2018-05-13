package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.User;

import java.util.Map;

public interface UserDataService {

    Map<String, Object> getUserScore(User user);

    Map<String, Object> getUserHomeFeed(User user);
}
