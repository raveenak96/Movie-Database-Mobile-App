package com.example.uscfilms;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder>{

    private List<ReviewData> reviewItems;

    public ReviewAdapter(List<ReviewData> reviewItems) { this.reviewItems = reviewItems; }

    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout,null);
        inflate.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.WRAP_CONTENT));
        return new ReviewAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ReviewAdapter.ViewHolder viewHolder, final int position) {
        final ReviewData reviewItem = reviewItems.get(position);
        String ratingFive = String.valueOf(reviewItem.getRating())+"/5";
        viewHolder.rating.setText(ratingFive);
        viewHolder.ratingStar.setText(Html.fromHtml("&#9733;"));
        String heading = "by "+reviewItem.getAuthor()+" on "+reviewItem.getDate();
        viewHolder.heading.setText(heading);
        String content = reviewItem.getContent();
        viewHolder.content.setText(content);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(),ReviewsActivity.class);
                intent.putExtra("rating",ratingFive);
                intent.putExtra("heading",heading);
                intent.putExtra("content",content);
                v.getContext().startActivity(intent);

            }
        });

    }

    public int getItemCount() {
        return reviewItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        TextView rating;
        TextView heading;
        TextView content;
        TextView ratingStar;

        public ViewHolder(View itemView) {
            super(itemView);
            rating = itemView.findViewById(R.id.review_card_rating);
            heading = itemView.findViewById(R.id.review_card_heading);
            content = itemView.findViewById(R.id.review_card_content);
            ratingStar = itemView.findViewById(R.id.review_card_star);
            this.itemView = itemView;
        }

    }
}
