package com.example.uscfilms;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;
import com.example.uscfilms.ui.home.HomeFragmentDirections;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class SliderAdapter extends SliderViewAdapter<SliderAdapter.SliderAdapterViewHolder> {

    // list for storing urls of images.
    private final List<SliderData> mSliderItems;

    // Constructor
    public SliderAdapter( ArrayList<SliderData> sliderDataArrayList) {
        this.mSliderItems = sliderDataArrayList;
    }

    // We are inflating the slider_layout
    // inside on Create View Holder method.
    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout, null);
        return new SliderAdapterViewHolder(inflate);
    }

    // Inside on bind view holder we will
    // set data to item of Slider View.
    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, final int position) {

        final SliderData sliderItem = mSliderItems.get(position);

        // Glide is use to load image
        // from url in your imageview.
        GlideApp.with(viewHolder.itemView)
                .load(sliderItem.getImgUrl())
                .apply(RequestOptions.bitmapTransform(new jp.wasabeef.glide.transformations.BlurTransformation(25, 2)))
                .into(viewHolder.blurredViewBackground);
        GlideApp.with(viewHolder.itemView)
                .load(sliderItem.getImgUrl())
                .centerCrop()
                .into(viewHolder.imageViewBackground);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemId = sliderItem.getImgId();

                Intent intent = new Intent(v.getContext(),DetailsActivity.class);
                intent.putExtra("itemId",itemId);
                intent.putExtra("mediaType",sliderItem.getMediaType());
                v.getContext().startActivity(intent);

            }
        });
    }

    // this method will return
    // the count of our list.
    @Override
    public int getCount() {
        return mSliderItems.size();
    }

    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        // Adapter class for initializing
        // the views of our slider view.
        View itemView;
        ImageView imageViewBackground;
        ImageView blurredViewBackground;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.sliderImage);
            blurredViewBackground = itemView.findViewById(R.id.sliderImageBlurred);
            this.itemView = itemView;
        }
    }
}
