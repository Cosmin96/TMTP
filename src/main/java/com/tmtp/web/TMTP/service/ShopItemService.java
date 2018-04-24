package com.tmtp.web.TMTP.service;

import com.tmtp.web.TMTP.entity.ShopItem;

import java.util.List;

public interface ShopItemService {

    ShopItem retrieveItemById(String id);

    List<ShopItem> retrieveAllItems();
}
