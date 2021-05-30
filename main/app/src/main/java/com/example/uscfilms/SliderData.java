package com.example.uscfilms;

public class SliderData {

    private String imgUrl;
    private String imgId;
    private String mediaType;

    // Constructor method.
    public SliderData(String imgUrl,String imgId,String mediaType) {
        this.imgUrl = imgUrl;
        this.imgId = imgId;
        this.mediaType = mediaType;
    }

    // Getter method
    public String getImgUrl() {
        return imgUrl;
    }

    // Setter method
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    // Getter method
    public String getImgId() {
        return imgId;
    }

    // Setter method
    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getMediaType() { return mediaType; }
}
