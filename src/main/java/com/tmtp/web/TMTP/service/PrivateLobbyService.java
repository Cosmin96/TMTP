package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.PrivateLobby;

import java.util.List;

public interface PrivateLobbyService {

    public PrivateLobby findById(String id);

    public PrivateLobby findByCreator(String creator);

    public void createLobby(PrivateLobby privateLobby);

    public List<PrivateLobby> retrieveAll();

    public void updateLobby(PrivateLobby privateLobby);
}
