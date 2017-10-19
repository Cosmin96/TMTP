package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.ShopItem;
import com.tmtp.web.TMTP.repository.ShopItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopItemServiceImpl implements ShopItemService{

    @Autowired
    private ShopItemRepository shopItemRepository;

    @Override
    public ShopItem retrieveItemById(String id){
        return shopItemRepository.findById(id);
    }

    @Override
    public List<ShopItem> retrieveAllItems(){
        return shopItemRepository.findAll();
    }
}
