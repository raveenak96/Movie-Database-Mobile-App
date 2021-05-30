package com.example.uscfilms;

public class WatchListData {
    private String imgUrl;
    private String imgId;
    private String mediaType;
    private String title;

    // Constructor method.
    public WatchListData(String title,String imgUrl,String imgId,String mediaType) {
        this.imgUrl = imgUrl;
        this.imgId = imgId;
        this.mediaType = mediaType;
        this.title = title;
    }

    // Getter method
    public String getImgUrl() {
        return imgUrl;
    }


    // Getter method
    public String getImgId() {
        return imgId;
    }

    public String getMediaType() { return mediaType; }

    public String getTitle() { return title; }
}
