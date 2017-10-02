package com.tmtp.web.TMTP.web.formobjects;

public class OverlayForm {

    private String overlayName;

    public String getOverlayName() {
        return overlayName;
    }

    public void setOverlayName(String overlayName) {
        this.overlayName = overlayName;
    }

    @Override
    public String toString() {
        return "OverlayForm{" +
                "overlayName='" + overlayName + '\'' +
                '}';
    }
}
