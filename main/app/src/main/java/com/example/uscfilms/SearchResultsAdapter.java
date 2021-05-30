package com.example.uscfilms;

import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;

import org.w3c.dom.Text;

import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private List<SearchResultsData> searchResults;

    public SearchResultsAdapter(List<SearchResultsData> searchResults) { this.searchResults = searchResults; }

    public SearchResultsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_layout, null);
        return new SearchResultsAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(SearchResultsAdapter.ViewHolder viewHolder, final int position) {
        final SearchResultsData resultItem = searchResults.get(position);
        GlideApp.with(viewHolder.itemView)
                .load(resultItem.getImgUrl())
                .placeholder(R.color.white)
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(viewHolder.imageViewBackground);
        viewHolder.titleView.setText(resultItem.getTitle());
        viewHolder.ratingView.setText(resultItem.getRating());
        viewHolder.dateView.setText("("+resultItem.getDate()+")");
        viewHolder.mediaTypeView.setText(resultItem.getMediaType());
        viewHolder.ratingStar.setText(Html.fromHtml("&#9733;"));
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = resultItem.getId();
                Intent intent = new Intent(v.getContext(),DetailsActivity.class);
                intent.putExtra("itemId",id);
                intent.putExtra("mediaType",resultItem.getMediaType());
                v.getContext().startActivity(intent);
            }
        } );
    }

    public int getItemCount() {
        return searchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView mediaTypeView;
        TextView dateView;
        TextView ratingView;
        TextView titleView;
        TextView ratingStar;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.search_result_image);
            mediaTypeView = itemView.findViewById(R.id.search_result_media_type);
            dateView = itemView.findViewById(R.id.search_result_date);
            ratingView = itemView.findViewById(R.id.search_result_rating);
            titleView = itemView.findViewById(R.id.search_result_title);
            ratingStar = itemView.findViewById(R.id.search_result_star);
            this.itemView = itemView;
        }

    }

}
