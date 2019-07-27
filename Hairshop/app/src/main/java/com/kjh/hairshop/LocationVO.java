package com.kjh.hairshop;

public class LocationVO implements Comparable<LocationVO> {

    private int nickName_idx;
    private String name;
    private Double address;
    private String photo;
    private String info;
    private int good;

    LocationVO(int nickName_idx, String name, Double address, String photo, String info, int good) {
        setNickName_idx(nickName_idx);
        setName(name);
        setAddress(address);
        setPhoto(photo);
        setInfo(info);
        setGood(good);
    }

    @Override
    public int compareTo(LocationVO locationVO) {

        return this.address.compareTo(locationVO.address);
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

    public Double getAddress() {
        return address;
    }

    public void setAddress(Double address) {
        this.address = address;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }
}
