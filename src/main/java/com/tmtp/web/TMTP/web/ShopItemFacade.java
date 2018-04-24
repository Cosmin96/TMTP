package com.tmtp.web.TMTP.web;

import com.tmtp.web.TMTP.entity.ShopItem;
import com.tmtp.web.TMTP.service.ShopItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShopItemFacade {

    @Autowired
    private ShopItemService shopItemService;

    public ShopItem retrieveItemById(String id){
        return shopItemService.retrieveItemById(id);
    }

    public List<ShopItem> retrieveAllItems(){
        return shopItemService.retrieveAllItems();    }
}
