package com.tmtp.web.TMTP.entity;


import org.springframework.data.annotation.Id;

public class VideoPosts {

    @Id
    private String id;
    private String link;
    private String description;
    private String creator;
    private User user;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "VideoPosts{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", creator='" + creator + '\'' +
                ", user=" + user +
                '}';
    }
}
