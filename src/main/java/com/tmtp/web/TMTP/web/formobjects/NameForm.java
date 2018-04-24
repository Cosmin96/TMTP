package com.tmtp.web.TMTP.web.formobjects;

public class NameForm {

    public String fname;
    public String lname;

//    public NameForm(String fname, String lname) {
//        this.fname = fname;
//        this.lname = lname;
//    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    @Override
    public String toString() {
        return "NameForm{" +
                "fname='" + fname + '\'' +
                ", lname='" + lname + '\'' +
                '}';
    }
}
