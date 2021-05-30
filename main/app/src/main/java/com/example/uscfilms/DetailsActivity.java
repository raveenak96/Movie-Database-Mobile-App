package com.example.uscfilms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.borjabravo.readmoretextview.ReadMoreTextView;
import com.example.uscfilms.ui.watchlist.WatchlistFragment;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;

public class DetailsActivity extends AppCompatActivity implements YouTubePlayer.OnInitializedListener {

    private String id;
    private String mediaType;
    private RequestQueue queue;
    public String backendBase;
    public String videoKey;
    public String title;
    boolean topLoaded = false;
    boolean detailsLoaded = false;
    boolean reviewsLoaded = false;
    boolean castLoaded = false;
    boolean recLoaded = false;
    private static final int RECOVERY_DIALOG_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }
        Intent intent = getIntent();
        id = intent.getStringExtra("itemId");
        mediaType = intent.getStringExtra("mediaType");
        title = "Item";
        backendBase = Globals.getInstance().getBackend();
        queue = queue = Volley.newRequestQueue(this);
        queue.getCache().clear();
        videoKey = "";
        createYoutubeAndDetails();
        createCast();
        createReviews();
        createRecommended();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void checkDataLoaded() {
        if(topLoaded && detailsLoaded && reviewsLoaded && castLoaded && recLoaded) {
            ((LinearLayout) findViewById(R.id.detailsLinear)).setVisibility(View.VISIBLE);
            ((ProgressBar) findViewById(R.id.progress_bar_details)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.loading_text_view_details)).setVisibility(View.GONE);
        }
    }

    public void makeObjectRequest( String endpoint,final VolleyCallbackObject volleyCallback) {
        String backendUrl = backendBase + endpoint;
        System.out.println(backendUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, backendUrl, (JSONObject) null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                volleyCallback.onSuccess(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerParams = new HashMap<String, String>();
                headerParams.put("Content-Type", "application/x-www-form-urlencoded");
                return headerParams;
            }
        };
        queue.getCache().clear();
        queue.add(jsonObjectRequest);
    }

    public void makeArrayRequest( String endpoint,final VolleyCallback volleyCallback) {

        String backendUrl = backendBase + endpoint;
        System.out.println(backendUrl);

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(Request.Method.GET, backendUrl, (JSONObject) null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                volleyCallback.onSuccess(response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.toString());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerParams = new HashMap<String, String>();
                headerParams.put("Content-Type", "application/x-www-form-urlencoded");
                return headerParams;
            }
        };
        queue.getCache().clear();
        queue.add(jsonObjectRequest);
    }

    public void createYoutubeAndDetails() {
        makeObjectRequest("/videos?media_type="+mediaType+"&id="+id,new VolleyCallbackObject() {
            @Override
            public void onSuccess(JSONObject video) {
                if (video.toString().equals("{}")) {
                    setupBackdrop();
                } else {
                    setupYoutube(video);
                }
            }

        });

    }

    public void setupYoutube(JSONObject video) {
        //make the video player
        try {
            videoKey = video.getString("key");
        } catch (JSONException e) {

        }
        //FrameLayout layout = findViewById(R.id.youtube_frame_layout);
        YouTubePlayerFragment youTubePlayerFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
        //layout.addView(youTubePlayerView);
        //getLifecycle().addObserver(youTubePlayerView);
        youTubePlayerFragment.initialize("",this);

        /*youTubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NotNull YouTubePlayer youTubePlayer) {
                try {
                    String videoId = video.getString("key");
                    youTubePlayer.cueVideo(videoId,0);
                } catch (JSONException e) {

                }

            }
        });*/
        topLoaded = true;
        makeArrayRequest("/details?media_type="+mediaType+"&id="+id,new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray details) {
                try {
                    setupDetails(details.getJSONObject(0));

                } catch (JSONException e) {
                    setupDetails(new JSONObject());
                }
            }
        });

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        if(!b) {
            youTubePlayer.cueVideo(videoKey);
        }

    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format("There was an error initializing the YouTubePlayer (%1$s)", youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
    public void setupBackdrop() {
        makeArrayRequest("/details?media_type="+mediaType+"&id="+id,new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray details) {
                try {
                    JSONObject itemDetails = details.getJSONObject(0);
                    loadBackdrop(itemDetails);
                    setupDetails(itemDetails);

                } catch (JSONException e) {

                }
            }
        });
    }

    public void loadBackdrop(JSONObject details) {
        try {
            String backdropPath = details.getString("backdrop_path");
            FrameLayout layout = (FrameLayout) findViewById(R.id.youtube_frame_layout);
            ImageView imageView = new ImageView(this);
            GlideApp.with(this)
                    .load(backdropPath)
                    .override(1400,1400)
                    .into(imageView);
            layout.addView(imageView);
            topLoaded = true;
            checkDataLoaded();
        } catch (JSONException e) {

        }

    }
    public void setupDetails(JSONObject details) {
        try {
            title = details.getString("title");
            TextView titleView = findViewById(R.id.details_item_title);
            titleView.setText(title);
        } catch(JSONException e) {

        }
        try {
            String overview = details.getString("overview");
            if(overview.length()>0) {
                ((TextView) findViewById(R.id.details_overview_title)).setVisibility(View.VISIBLE);
                ReadMoreTextView overviewTextView = (ReadMoreTextView) findViewById(R.id.details_overview);
                overviewTextView.setText(overview);
                overviewTextView.setVisibility(View.VISIBLE);
            }

            //add show more
        } catch (JSONException e) {

        }

        TextView genresTitle = findViewById(R.id.details_genres_title);
        TextView genresList = findViewById(R.id.details_genres);
        try {
          JSONArray genres = details.getJSONArray("genres");

          if(genres.length()>0) {
              String genreList = "";
              for(int i=0;i<genres.length();i++) {
                  genreList = genreList + genres.get(i).toString();
                  if (i<genres.length()-1) {
                      genreList = genreList + ", ";
                  }
              }
              genresTitle.setText("Genres");
              genresList.setText(genreList);
          } else {
              genresTitle.setVisibility(View.GONE);
              genresList.setVisibility(View.GONE);
          }

        } catch (JSONException e) {
            genresTitle.setVisibility(View.GONE);
            genresList.setVisibility(View.GONE);
        }

        try {
            String year = "";
            if(mediaType.equals("movie")) {
                year = details.getString("release_date");
            } else {
                year = details.getString("first_air_date");
            }
            if(year.length()>0) {
                ((TextView)findViewById(R.id.details_year_title)).setVisibility(View.VISIBLE);
                ((TextView)findViewById(R.id.details_year)).setText(year);
                ((TextView)findViewById(R.id.details_year)).setVisibility(View.VISIBLE);
            }

        } catch(JSONException e) {

        }

        //handle watchlist button
        WatchListHelper watchListHelper = new WatchListHelper();
        ImageView watchListButton = (ImageView) findViewById(R.id.details_watchlist_button);
        if(watchListHelper.isInWatchlist(this.getApplicationContext(),id)) {

            watchListButton.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24);
        } else {
            watchListButton.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
        }
        watchListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(watchListHelper.isInWatchlist(v.getContext(),id)) {
                    watchListHelper.removeItem(v.getContext(),id);
                    String toastText = "";
                    toastText = title + " was removed from Watchlist";
                    Toast.makeText(v.getContext(), toastText, Toast.LENGTH_SHORT).show();
                    watchListButton.setImageResource(R.drawable.ic_baseline_add_circle_outline_24);
                } else {
                    String poster_path = "https://cinemaone.net/images/movie_placeholder.png";
                    try {
                        poster_path = details.getString("poster_path");

                    } catch (JSONException e) {

                    }
                    WatchListData watchListItem = new WatchListData(title,poster_path,id,mediaType);
                    watchListHelper.addItem(v.getContext(),watchListItem);
                    String toastText = "";
                    toastText = title + " was added to Watchlist";
                    Toast.makeText(v.getContext(), toastText, Toast.LENGTH_SHORT).show();
                    watchListButton.setImageResource(R.drawable.ic_baseline_remove_circle_outline_24);
                }


            }
        });

        //change to share dialog
        String shareUrl = "https://themoviedb.org/"+mediaType+"/"+id;
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(shareUrl))
                .build();
        ShareDialog shareDialog = new ShareDialog(this);

        ImageView shareFB = (ImageView) findViewById(R.id.fb_share_custom);
        shareFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent content = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(shareUrl))
                            .build();
                    shareDialog.show(content);
                }
            }
        });
        ImageView twitterButton = (ImageView) findViewById(R.id.twitter_share_button);
        twitterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String twitterUrl = "http://www.twitter.com/intent/tweet?url="+shareUrl+"&text=Check this out!";
                Intent i = new Intent((Intent.ACTION_VIEW));
                i.setData(Uri.parse(twitterUrl));
                startActivity(i);
            }
        });

        detailsLoaded = true;
        checkDataLoaded();
    }

    public void createCast() {
        makeArrayRequest("/cast?media_type="+mediaType+"&id="+id,new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray cast) {
                setupCast(cast);
            }
        });
    }

    public void setupCast(JSONArray cast) {
        TextView castTitle = findViewById(R.id.cast_title);
        if(cast.length()>0) {
            castTitle.setText("Cast");
            ArrayList<CastData> castDataArrayList = new ArrayList<>();
            for(int i=0;i<cast.length();i++) {
                try {

                    String imgUrl = cast.getJSONObject(i).getString("profile_path");
                    String name = cast.getJSONObject(i).getString("title");
                    castDataArrayList.add(new CastData(imgUrl,name));
                } catch (JSONException e) {
                    continue;
                }
            }
            RecyclerView recyclerView = findViewById(R.id.rvCast);
            recyclerView.setLayoutManager(new GridLayoutManager(this,3));
            CastAdapter adapter = new CastAdapter(castDataArrayList);
            recyclerView.setAdapter(adapter);
        } else {
            castTitle.setVisibility(View.GONE);
            ((RelativeLayout) findViewById(R.id.rvCastLayout)).setVisibility(View.GONE);
        }
        castLoaded = true;
        checkDataLoaded();

    }

    public void createReviews() {
        makeArrayRequest("/reviews?media_type="+mediaType+"&id="+id,new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray reviews) {
                setupReviews(reviews);
            }
        });
    }

    public void setupReviews(JSONArray reviews) {
        TextView reviewsTitle = (TextView) findViewById(R.id.reviews_title);
        if(reviews.length()>0) {
            reviewsTitle.setText("Reviews");
            ArrayList<ReviewData> reviewDataArrayList = new ArrayList<>();
            for(int i=0;i<reviews.length();i++) {
                String author = "";
                String content = "";
                int rating = 0;
                String date = "";
                try {
                    JSONObject reviewItem = reviews.getJSONObject(i);
                    author = reviewItem.getString("author");
                    content = reviewItem.getString("content");
                    rating = reviewItem.getInt("rating");
                    date = reviewItem.getString("created_at");
                    reviewDataArrayList.add(new ReviewData(author,content,rating,date));
                } catch(JSONException e) {
                    continue;
                }

            }
            RecyclerView recyclerView = findViewById(R.id.rvReview);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            ReviewAdapter adapter = new ReviewAdapter(reviewDataArrayList);
            recyclerView.setAdapter(adapter);

        } else {
            reviewsTitle.setVisibility(View.GONE);
            ((RelativeLayout) findViewById(R.id.rvReviewLayout)).setVisibility(View.GONE);
        }
        reviewsLoaded = true;
        checkDataLoaded();
    }

    public void createRecommended() {
        makeArrayRequest("/recommendations?media_type="+mediaType+"&id="+id,new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray items) {
                setupRecommended(items);
            }
        });
    }

    public void setupRecommended(JSONArray items) {
        ArrayList<SliderData> picksDataArrayList = new ArrayList<>();
        if(items.length()>0) {
            for(int i=0;i<items.length();i++) {
                try {
                    JSONObject pickItem = items.getJSONObject(i);
                    String itemId = String.valueOf(pickItem.getInt("id"));
                    picksDataArrayList.add(new SliderData(pickItem.getString("poster_path"),itemId,mediaType));
                } catch(JSONException e) {

                }
            }
            ((TextView) findViewById(R.id.recTitle)).setVisibility(View.VISIBLE);
            ((HorizontalScrollView) findViewById(R.id.recScrollView)).setVisibility(View.VISIBLE);
            RecyclerView recyclerView = findViewById(R.id.rvRecommended);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            RecommendationsAdapter adapter = new RecommendationsAdapter(picksDataArrayList);
            recyclerView.setAdapter(adapter);
        } else {

        }


        recLoaded = true;
        checkDataLoaded();
    }

}
