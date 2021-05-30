package com.example.uscfilms;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableString;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.ArrayList;
import java.util.List;

public class HorizontalCardsAdapter extends RecyclerView.Adapter<HorizontalCardsAdapter.ViewHolder> {

    private List<WatchListData> cardItems;

    public HorizontalCardsAdapter(List<WatchListData> cardItems) {
        this.cardItems = cardItems;
    }

    @Override
    public HorizontalCardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.small_card_layout, null);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {
        WatchListData cardItem = cardItems.get(position);
        GlideApp.with(viewHolder.itemView)
                .load(cardItem.getImgUrl())
                .fitCenter()
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

        viewHolder.popupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context wrapper = new ContextThemeWrapper(v.getContext(),R.style.PopupMenuTheme);
                PopupMenu popupMenu = new PopupMenu(wrapper,viewHolder.popupButton);
                WatchListHelper watchListHelper = new WatchListHelper();
                if(watchListHelper.isInWatchlist(v.getContext(),cardItem.getImgId())) {
                    popupMenu.getMenu().add(Menu.NONE,3,Menu.FIRST,"Remove from Watchlist");

                } else {
                    popupMenu.getMenu().add(Menu.NONE,3,Menu.FIRST,"Add to Watchlist");
                }
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String title = item.getTitle().toString();
                        String tmdb = "https://themoviedb.org/" + cardItem.getMediaType() + "/" + cardItem.getImgId();
                        WatchListHelper watchListHelper = new WatchListHelper();
                        if (title.equals("Open in TMDB")) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(tmdb));
                            v.getContext().startActivity(browserIntent);
                        } else if (title.equals("Share on Facebook")) {
                            ShareDialog shareDialog = new ShareDialog((AppCompatActivity) v.getContext());

                            if(ShareDialog.canShow(ShareLinkContent.class)) {
                                ShareLinkContent content = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse(tmdb))
                                        .build();
                                shareDialog.show(content);
                            }
                        } else if (title.equals("Share on Twitter")) {
                            String twitterUrl = "http://www.twitter.com/intent/tweet?url="+tmdb+"&text=Check this out!";
                            Intent i = new Intent((Intent.ACTION_VIEW));
                            i.setData(Uri.parse(twitterUrl));
                            v.getContext().startActivity(i);
                        } else if (title.equals("Add to Watchlist")) {
                            watchListHelper.addItem(v.getContext(),cardItem);
                            String toastText = cardItem.getTitle() + " was added to Watchlist";
                            Toast.makeText(v.getContext(), toastText, Toast.LENGTH_SHORT).show();
                        } else if(title.equals("Remove from Watchlist")) {
                            watchListHelper.removeItem(v.getContext(),cardItem.getImgId());
                            String toastText = cardItem.getTitle() + " was removed from Watchlist";
                            Toast.makeText(v.getContext(), toastText, Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
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
