package com.tmtp.web.TMTP.entity;

import org.springframework.data.annotation.Id;

import java.util.Set;

public class User {

    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String profile;
    private String overlay;
    private String password;
    private PlayerKit playerKit;
    private Points points;
    private Set<Role> roles;

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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
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

    public Points getPoints() {
        return points;
    }

    public void setPoints(Points points) {
        this.points = points;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
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
                ", playerKit=" + playerKit +
                ", points=" + points +
                ", roles=" + roles +
                '}';
    }
}
