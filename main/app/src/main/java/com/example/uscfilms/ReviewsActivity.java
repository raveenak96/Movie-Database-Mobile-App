package com.example.uscfilms;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class ReviewsActivity extends AppCompatActivity {

    private String rating;
    private String heading;
    private String content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        Intent intent = getIntent();
        rating = intent.getStringExtra("rating");
        heading = intent.getStringExtra("heading");
        content = intent.getStringExtra("content");
        createReview();
    }

    public void createReview() {
        ((TextView) findViewById(R.id.review_rating)).setText(rating);
        ((TextView) findViewById(R.id.review_star)).setText(Html.fromHtml("&#9733;"));
        ((TextView) findViewById(R.id.review_heading)).setText(heading);
        ((TextView) findViewById(R.id.review_content)).setText(content);
    }
}