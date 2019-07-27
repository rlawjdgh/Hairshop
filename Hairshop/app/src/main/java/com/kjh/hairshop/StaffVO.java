package com.kjh.hairshop;

public class StaffVO {

    private int staff_idx;
    private int nickName_idx;
    private String name;
    private String info;
    private String grade;
    private String photo;


    public int getStaff_idx() {
        return staff_idx;
    }

    public void setStaff_idx(int staff_idx) {
        this.staff_idx = staff_idx;
    }

    public int getNickName_idx() {
        return nickName_idx;
    }

    public void setNickName_idx(int nickName_idx) {
        this.nickName_idx = nickName_idx;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
