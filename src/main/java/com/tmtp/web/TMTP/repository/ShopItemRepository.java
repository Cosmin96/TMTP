package com.tmtp.web.TMTP.repository;

import com.tmtp.web.TMTP.entity.ShopItem;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ShopItemRepository extends MongoRepository<ShopItem, String>{
    ShopItem findById(String id);
}
