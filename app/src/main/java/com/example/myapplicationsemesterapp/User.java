package com.example.myapplicationsemesterapp;

public class User {
    public String dob, gender, mobile;

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    //Constructor
    public User(){};

    public User(String textDoB, String textGender, String textMobile){

        this.dob = textDoB;
        this.gender = textGender;
        this.mobile = textMobile;
    }
}
