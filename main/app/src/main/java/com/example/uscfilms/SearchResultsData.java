package com.example.uscfilms;

import java.text.DecimalFormat;

public class SearchResultsData {

    private String imgUrl;
    private String id;
    private String title;
    private String mediaType;
    private String rating;
    private String date;

    public SearchResultsData(String imgUrl,String id, String title, String mediaType,Double rating,String date) {
        this.imgUrl = imgUrl;
        this.id = id;
        this.title = title;
        this.mediaType = mediaType;
        DecimalFormat df = new DecimalFormat("#.#");
        this.rating = df.format(rating/2);
        this.date = date;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getId() {
        return id;
    }

    public String getMediaType() {
        return this.mediaType;
    }

    public String getRating() {
        return rating;
    }

    public String getTitle() { return title; }
    public String getDate() { return date; }
}

