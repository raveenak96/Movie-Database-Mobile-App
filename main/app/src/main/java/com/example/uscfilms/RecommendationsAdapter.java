package com.example.uscfilms;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

import java.util.List;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.ViewHolder> {

    private List<SliderData> cardItems;

    public RecommendationsAdapter(List<SliderData> cardItems) {
        this.cardItems = cardItems;
    }

    @Override
    public RecommendationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_card_layout, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        final SliderData cardItem = cardItems.get(position);
        viewHolder.imageViewBackground.setForeground(null);
        GlideApp.with(viewHolder.itemView)
                .load(cardItem.getImgUrl())
                .centerCrop()
                .into(viewHolder.imageViewBackground);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemId = cardItem.getImgId();
                Intent intent = new Intent(v.getContext(),DetailsActivity.class);
                intent.putExtra("itemId",itemId);
                intent.putExtra("mediaType",cardItem.getMediaType());
                v.getContext().startActivity(intent);
            }
        });

        viewHolder.popupButton.setVisibility(View.GONE);

    }

    public int getItemCount() {
        return cardItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        Button popupButton;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.smallCardImage);
            popupButton = itemView.findViewById(R.id.popup_button);
            this.itemView = itemView;
        }

    }
}

