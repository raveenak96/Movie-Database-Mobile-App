package com.example.uscfilms;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CastAdapter  extends RecyclerView.Adapter<CastAdapter.ViewHolder> {

    private List<CastData> castItems;

    public CastAdapter(List<CastData> castItems) {
        this.castItems = castItems;
    }

    @Override
    public CastAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.cast_layout, null);
        return new CastAdapter.ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(CastAdapter.ViewHolder viewHolder, final int position) {
        final CastData castItem = castItems.get(position);
        String imgUrl = castItem.getImgUrl();
        if(imgUrl.equals("")) {
            GlideApp.with(viewHolder.itemView)
                    .load(viewHolder.itemView.getContext().getDrawable(R.drawable.cast_placeholder))
                    .into(viewHolder.imageViewBackground);
        } else {
            GlideApp.with(viewHolder.itemView)
                    .load(castItem.getImgUrl())
                    .into(viewHolder.imageViewBackground);
        }
        viewHolder.castMemberName.setText(castItem.getName());

    }
    public int getItemCount() {
        return castItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView castMemberName;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.cast_image);
            castMemberName = itemView.findViewById(R.id.cast_member_name);
            this.itemView = itemView;
        }

    }
}

