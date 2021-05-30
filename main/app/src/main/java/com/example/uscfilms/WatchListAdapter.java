package com.example.uscfilms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class WatchListAdapter  extends RecyclerView.Adapter<WatchListAdapter.ViewHolder> implements ItemMoveCallback.ItemTouchHelperContract {

    private ArrayList<WatchListData> watchListItems;
    private TextView noWatchListText;
    public class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView mediaTypeView;
        Button removeButton;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.watchlist_image);
            mediaTypeView = itemView.findViewById(R.id.watchlist_mediaType);
            removeButton = itemView.findViewById(R.id.watchlist_remove_button);
            this.itemView = itemView;
        }
    }

    public WatchListAdapter(ArrayList<WatchListData> watchListItems,TextView noWatchListText) {
        this.watchListItems = watchListItems;
        this.noWatchListText = noWatchListText;
    }

    @Override
    public WatchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.watchlist_card_layout, parent, false);
        return new WatchListAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WatchListAdapter.ViewHolder viewHolder,int position) {
        WatchListData watchListItem = watchListItems.get(position);
        GlideApp.with(viewHolder.itemView)
                .load(watchListItem.getImgUrl())
                .centerCrop()
                .into(viewHolder.imageViewBackground);
        String mediaType = watchListItem.getMediaType();
        if(mediaType.equals("movie")) {
            mediaType = mediaType.substring(0,1).toUpperCase() + mediaType.substring(1);
        } else {
            mediaType = mediaType.toUpperCase();
        }

        viewHolder.mediaTypeView.setText(mediaType);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemId = watchListItem.getImgId();
                Intent intent = new Intent(v.getContext(),DetailsActivity.class);
                intent.putExtra("itemId",itemId);
                intent.putExtra("mediaType",watchListItem.getMediaType());
                v.getContext().startActivity(intent);
            }
        });
        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WatchListHelper watchListHelper = new WatchListHelper();
                ArrayList<WatchListData> newWatchList = watchListHelper.removeItem(v.getContext(),watchListItem.getImgId());
                String toastText = watchListItem.getTitle() + " was removed from Watchlist";
                Toast.makeText(v.getContext(), toastText, Toast.LENGTH_SHORT).show();
                watchListItems = newWatchList;
                notifyDataSetChanged();
                if(watchListItems.size()==0) {
                    noWatchListText.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(watchListItems != null) {
            return watchListItems.size();
        } else {
            return 0;
        }

    }

    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(watchListItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(watchListItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        final Gson gson = new Gson();
        String newWatchList = gson.toJson(watchListItems);
        editor.clear();
        editor.putString("watchlist",newWatchList);
        editor.commit();
    }

    @Override
    public void onRowSelected(WatchListAdapter.ViewHolder viewHolder) {


    }

    @Override
    public void onRowClear(WatchListAdapter.ViewHolder viewHolder) {


    }


}
