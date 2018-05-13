package com.tmtp.web.TMTP.entity;

public class UserRegistration extends UserInfo {

    private String image;
    private String inviteCode;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }
}
