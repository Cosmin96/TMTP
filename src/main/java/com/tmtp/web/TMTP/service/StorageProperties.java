package com.tmtp.web.TMTP.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "src/main/resources/static/img/profile";

    private String jacketsLocation = "src/main/resources/static/img/kits/jackets";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getJacketsLocation() {
        return jacketsLocation;
    }

    public void setJacketsLocation(String jacketsLocation) {
        this.jacketsLocation = jacketsLocation;
    }
}
