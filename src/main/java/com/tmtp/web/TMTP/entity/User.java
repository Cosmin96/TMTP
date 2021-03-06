package com.tmtp.web.TMTP.entity;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;

import java.util.List;
import java.util.Set;

public class User {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String profile;
    private String profileImageUrl;
    private String title;
    private String overlay;
    private String password;
    private int stadiumLevel;
    private String trophy;
    private Boolean admin;
    private Boolean banned;
    private Boolean privateLobby;
    private DateTime banTime;
    private PlayerKit playerKit;
    private Points points;
    private List<ShopItem> shopItems;
    private Set<Role> roles;
    private Team team;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PlayerKit getPlayerKit() {
        return playerKit;
    }

    public void setPlayerKit(PlayerKit playerKit) {
        this.playerKit = playerKit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getOverlay() {
        return overlay;
    }

    public void setOverlay(String overlay) {
        this.overlay = overlay;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStadiumLevel() {
        return stadiumLevel;
    }

    public void setStadiumLevel(int stadiumLevel) {
        this.stadiumLevel = stadiumLevel;
    }

    public String getTrophy() {
        return trophy;
    }

    public void setTrophy(String trophy) {
        this.trophy = trophy;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getPrivateLobby() {
        return privateLobby;
    }

    public void setPrivateLobby(Boolean privateLobby) {
        this.privateLobby = privateLobby;
    }

    public DateTime getBanTime() {
        return banTime;
    }

    public void setBanTime(DateTime banTime) {
        this.banTime = banTime;
    }

    public Boolean getBanned() {
        return banned;
    }

    public void setBanned(Boolean banned) {
        this.banned = banned;
    }

    public Points getPoints() {
        return points;
    }

    public void setPoints(Points points) {
        this.points = points;
    }

    public List<ShopItem> getShopItems() {
        return shopItems;
    }

    public void setShopItems(List<ShopItem> shopItems) {
        this.shopItems = shopItems;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", profile='" + profile + '\'' +
                ", overlay='" + overlay + '\'' +
                ", password='" + password + '\'' +
                ", stadiumLevel=" + stadiumLevel +
                ", admin=" + admin +
                ", banned=" + banned +
                ", banTime=" + banTime +
                ", playerKit=" + playerKit +
                ", points=" + points +
                ", roles=" + roles +
                ", team=" + team.teamName +
                '}';
    }
}
