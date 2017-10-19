package com.tmtp.web.TMTP.entity;

import org.springframework.data.annotation.Id;

public class ShopItem {

    @Id
    private String id;
    private String name;
    private String imgPath;
    private String pointPrice;
    private String gbpPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public String getPointPrice() {
        return pointPrice;
    }

    public void setPointPrice(String pointPrice) {
        this.pointPrice = pointPrice;
    }

    public String getGbpPrice() {
        return gbpPrice;
    }

    public void setGbpPrice(String gbpPrice) {
        this.gbpPrice = gbpPrice;
    }
}
