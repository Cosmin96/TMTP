package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.PrivateLobby;
import com.tmtp.web.TMTP.service.PrivateLobbyService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PrivateLobbyFacade {

    private final PrivateLobbyService privateLobbyService;

    public PrivateLobbyFacade(final PrivateLobbyService privateLobbyService) {
        this.privateLobbyService = privateLobbyService;
    }

    public PrivateLobby findById(String id){
        return privateLobbyService.findById(id);
    }

    public PrivateLobby findByCreator(String creator){
        return privateLobbyService.findByCreator(creator);
    }

    public void createLobby(PrivateLobby privateLobby){
        privateLobbyService.createLobby(privateLobby);
    }

    public List<PrivateLobby> retrieveAll(){
        return privateLobbyService.retrieveAll();
    }

    public void updateLobby(PrivateLobby privateLobby){
        privateLobbyService.updateLobby(privateLobby);
    }
}
