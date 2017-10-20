package com.tmtp.web.TMTP.entity;


import org.springframework.data.annotation.Id;

import java.util.List;

public class VideoPosts {

    @Id
    private String id;
    private String link;
    private String description;
    private String creator;
    private User user;
    private List<String> likeUsers;
    private List<String> dislikeUsers;
    private int likes;
    private int dislikes;
    private boolean grantPoint;
    private List<Comment> comments;

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

    public List<String> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(List<String> likeUsers) {
        this.likeUsers = likeUsers;
    }

    public List<String> getDislikeUsers() {
        return dislikeUsers;
    }

    public void setDislikeUsers(List<String> dislikeUsers) {
        this.dislikeUsers = dislikeUsers;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public void setDislikes(int dislikes) {
        this.dislikes = dislikes;
    }

    public boolean getGrantPoint() {
        return grantPoint;
    }

    public void setGrantPoint(boolean grantPoint) {
        this.grantPoint = grantPoint;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @Override
    public String toString() {
        return "VideoPosts{" +
                "id='" + id + '\'' +
                ", link='" + link + '\'' +
                ", description='" + description + '\'' +
                ", creator='" + creator + '\'' +
                ", user=" + user +
                ", comments=" + comments +
                '}';
    }
}
