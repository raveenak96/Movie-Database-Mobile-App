package com.example.uscfilms;

public class ReviewData {

    private String author;
    private String content;
    private int rating;
    private String date;


    // Constructor method.
    public ReviewData(String author,String content,int rating,String date) {
        this.author = author;
        this.content = content;
        this.rating = (int) Math.round(rating/2);
        this.date = date;
    }

    // Getter method
    public String getAuthor() {
        return author;
    }


    // Getter method
    public String getContent() {
        return content;
    }

    public int getRating() { return rating; }

    public String getDate() { return date; }
}
