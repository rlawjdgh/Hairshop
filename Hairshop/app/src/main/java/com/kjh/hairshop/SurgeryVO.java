package com.kjh.hairshop;

public class SurgeryVO {

    private int surgery_idx;
    private int nickName_idx;
    private int category;
    private String name;
    private int price;
    private String photo;


    public int getSurgery_idx() {
        return surgery_idx;
    }

    public void setSurgery_idx(int surgery_idx) {
        this.surgery_idx = surgery_idx;
    }

    public int getNickName_idx() {
        return nickName_idx;
    }

    public void setNickName_idx(int nickName_idx) {
        this.nickName_idx = nickName_idx;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
