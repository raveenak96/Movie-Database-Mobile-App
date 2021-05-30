package com.example.uscfilms;

public class CastData {

    private String imgUrl;
    private String name;

    // Constructor method.
    public CastData(String imgUrl,String name) {
        this.imgUrl = imgUrl;
        this.name = name;
    }

    // Getter method
    public String getImgUrl() {
        return imgUrl;
    }


    // Getter method
    public String getName() {
        return name;
    }


}
