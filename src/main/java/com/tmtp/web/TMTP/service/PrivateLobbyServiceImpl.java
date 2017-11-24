package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.PrivateLobby;
import com.tmtp.web.TMTP.repository.PrivateLobbyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrivateLobbyServiceImpl implements PrivateLobbyService {

    private final PrivateLobbyRepository privateLobbyRepository;

    public PrivateLobbyServiceImpl(final PrivateLobbyRepository privateLobbyRepository) {
        this.privateLobbyRepository = privateLobbyRepository;
    }

    @Override
    public PrivateLobby findById(String id){
        return privateLobbyRepository.findById(id);
    }

    @Override
    public PrivateLobby findByCreator(String creator){
        return privateLobbyRepository.findByCreator(creator);
    }

    @Override
    public void createLobby(PrivateLobby privateLobby){
        privateLobbyRepository.save(privateLobby);
    }

    @Override
    public List<PrivateLobby> retrieveAll(){
        return privateLobbyRepository.findAll();
    }

    @Override
    public void updateLobby(PrivateLobby privateLobby){
        privateLobbyRepository.save(privateLobby);
    }
}
