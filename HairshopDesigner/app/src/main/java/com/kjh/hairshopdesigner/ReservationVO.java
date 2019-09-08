package com.kjh.hairshopdesigner;

public class ReservationVO {

    private int reservation_idx;
    private String user_nickName;
    private String staff_name;
    private String staff_grade;
    private String cal_day;
    private String getTime;
    private String surgery_name;
    private int complete;

    public int getReservation_idx() {
        return reservation_idx;
    }

    public void setReservation_idx(int reservation_idx) {
        this.reservation_idx = reservation_idx;
    }

    public String getStaff_grade() {
        return staff_grade;
    }

    public void setStaff_grade(String staff_grade) {
        this.staff_grade = staff_grade;
    }

    public String getUser_nickName() {
        return user_nickName;
    }

    public void setUser_nickName(String user_nickName) {
        this.user_nickName = user_nickName;
    }

    public String getStaff_name() {
        return staff_name;
    }

    public void setStaff_name(String staff_name) {
        this.staff_name = staff_name;
    }

    public String getCal_day() {
        return cal_day;
    }

    public void setCal_day(String cal_day) {
        this.cal_day = cal_day;
    }

    public String getGetTime() {
        return getTime;
    }

    public void setGetTime(String getTime) {
        this.getTime = getTime;
    }

    public String getSurgery_name() {
        return surgery_name;
    }

    public void setSurgery_name(String surgery_name) {
        this.surgery_name = surgery_name;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }
}
