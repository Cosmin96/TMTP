package com.tmtp.web.TMTP.web.formobjects;

public class PassForm {

    private String oldPass;
    private String newPass;

    public String getOldPass() {
        return oldPass;
    }

    public void setOldPass(String oldPass) {
        this.oldPass = oldPass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }

    @Override
    public String toString() {
        return "PassForm{" +
                "oldPass='" + oldPass + '\'' +
                ", newPass='" + newPass + '\'' +
                '}';
    }
}
