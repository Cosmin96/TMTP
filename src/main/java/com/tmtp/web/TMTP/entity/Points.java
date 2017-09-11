package com.tmtp.web.TMTP.entity;

public class Points {

    private int green;
    private int yellow;
    private int red;

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getYellow() {
        return yellow;
    }

    public void setYellow(int yellow) {
        this.yellow = yellow;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    @Override
    public String toString() {
        return "Points{" +
                "green=" + green +
                ", yellow=" + yellow +
                ", red=" + red +
                '}';
    }
}
