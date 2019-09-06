package com.kjh.hairshopdesigner;

public class ReservationVO {

    private int reservation_idx;
    private String user_name;
    private String staff_name;
    private String cal_day;
    private String getTime;
    private String surgery_name;

    public int getReservation_idx() {
        return reservation_idx;
    }

    public void setReservation_idx(int reservation_idx) {
        this.reservation_idx = reservation_idx;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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
}
