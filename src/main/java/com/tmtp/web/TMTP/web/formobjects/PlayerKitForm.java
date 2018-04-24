package com.tmtp.web.TMTP.web.formobjects;

public class PlayerKitForm{

    private String jacket;
    private String shorts;
    private String socks;
    private String football;

    public String getJacket() {
        return jacket;
    }

    public void setJacket(String jacket) {
        this.jacket = jacket;
    }

    public String getShorts() {
        return shorts;
    }

    public void setShorts(String shorts) {
        this.shorts = shorts;
    }

    public String getSocks() {
        return socks;
    }

    public void setSocks(String socks) {
        this.socks = socks;
    }

    public String getFootball() {
        return football;
    }

    public void setFootball(String football) {
        this.football = football;
    }

    @Override
    public String toString() {
        return "PlayerKitForm{" +
                "jacket='" + jacket + '\'' +
                ", shorts='" + shorts + '\'' +
                ", socks='" + socks + '\'' +
                ", football='" + football + '\'' +
                '}';
    }
}
