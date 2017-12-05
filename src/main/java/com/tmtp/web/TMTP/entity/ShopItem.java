package com.tmtp.web.TMTP.entity;

import org.springframework.data.annotation.Id;

public class ShopItem{

    @Id
    private String id;
    private String name;
    private String type;
    private String imgPath;
    private int pointPrice;
    private int gbpPrice;
    private String gbpPriceFormatted;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getPointPrice() {
        return pointPrice;
    }

    public void setPointPrice(int pointPrice) {
        this.pointPrice = pointPrice;
    }

    public int getGbpPrice() {
        return gbpPrice;
    }

    public void setGbpPrice(int gbpPrice) {
        this.gbpPrice = gbpPrice;
    }

    public String getGbpPriceFormatted() {
        return gbpPriceFormatted;
    }

    public void setGbpPriceFormatted(String gbpPriceFormatted) {
        this.gbpPriceFormatted = gbpPriceFormatted;
    }

}
