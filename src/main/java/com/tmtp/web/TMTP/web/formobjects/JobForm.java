package com.tmtp.web.TMTP.web.formobjects;

import org.springframework.web.multipart.MultipartFile;

public class JobForm {

    private String title;
    private String contact;
    private String date;
    private String description;
    private String location;
    private MultipartFile imagePath;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public MultipartFile getImagePath() {
        return imagePath;
    }

    public void setImagePath(MultipartFile imagePath) {
        this.imagePath = imagePath;
    }
}
