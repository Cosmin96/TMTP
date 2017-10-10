package com.tmtp.web.TMTP.web.formobjects;

public class PointsForm {

    private int points;
    private String color;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "PointsForm{" +
                "points=" + points +
                ", color='" + color + '\'' +
                '}';
    }
}
