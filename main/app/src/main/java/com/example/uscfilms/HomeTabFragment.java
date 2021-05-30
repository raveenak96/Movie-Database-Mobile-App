package com.example.uscfilms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.uscfilms.ui.home.HomeFragment;
import com.smarteist.autoimageslider.SliderView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;



public class HomeTabFragment extends Fragment {

    private static String mediaType;
    private String backendBase;
    private RequestQueue queue;
    boolean popularCreated = false;
    boolean topRatedCreated = false;
    boolean mainSliderCreated = false;
    JSONArray popularItems;
    public HomeTabFragment() { }
    public static HomeTabFragment newInstance(String media) {
        HomeTabFragment fragment = new HomeTabFragment();
        Bundle args = new Bundle();
        args.putString("mediaType", media);
        mediaType = media;
        Globals g = Globals.getInstance();
        String backendBase = g.getBackend();
        args.putString("backendBase",backendBase);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(mediaType.equals("movie")) {
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) {
                ab.hide();
            }
            ((HomeFragment) getParentFragment()).getView().findViewById(R.id.tabs).setVisibility(View.GONE);
        }

        return inflater.inflate(R.layout.fragment_home_tab, container, false);


    }
    @Override
    public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {
        if(mediaType.equals("movie")) {
            ((ProgressBar) view.findViewById(R.id.progress_bar)).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.loading_text_view)).setVisibility(View.VISIBLE);
        }

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        createMainSlider(view);
        createTopRated(view);
        createPopular(view);
        TextView footer = (TextView) view.findViewById(R.id.powered_by);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://themoviedb.org/"));
                v.getContext().startActivity(browserIntent);
            }
        });
        ConstraintLayout layout = getActivity().findViewById(R.id.container);
        layout.setVisibility(View.VISIBLE);
    }

    public void makeRequest( String endpoint,final VolleyCallback volleyCallback) {
        String backendBase = getArguments().getString("backendBase");

        String backendUrl = backendBase + endpoint;

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
            queue.add(jsonObjectRequest);
    }
    public void createMainSlider(View view) {
        //String mediaType = getArguments().getString("mediaType");
        HashMap<String,String> params = new HashMap<String,String>();
        if(mediaType.equals("tv")) {
            //params.put("media_type", mediaType);
            makeRequest("/trending?media_type="+mediaType,new VolleyCallback() {
                @Override
                public void onSuccess(JSONArray items) {
                    setupMainSlider(items,view);
                }
            });
        } else if (mediaType.equals("movie")) {
            makeRequest("/curr-playing-mov",new VolleyCallback() {
                @Override
                public void onSuccess(JSONArray items) {
                    setupMainSlider(items,view);
                }
            });
        }

    }

    private void setupMainSlider(JSONArray items,View view) {

        ArrayList<SliderData> sliderDataArrayList = new ArrayList<>();

        SliderView sliderView = view.findViewById(R.id.slider);
        for(int i=0;i<items.length();i++) {
            try {

                String imgUrl = items.getJSONObject(i).getString("poster_path");
                String imgId = String.valueOf(items.getJSONObject(i).getInt("id"));
                sliderDataArrayList.add(new SliderData(imgUrl,imgId,mediaType));
            } catch (JSONException e) {
                continue;
            }

        }
        SliderAdapter adapter = new SliderAdapter(sliderDataArrayList);
        sliderView.setAutoCycleDirection(SliderView.LAYOUT_DIRECTION_LTR);
        sliderView.setSliderAdapter(adapter);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);
        sliderView.startAutoCycle();
        mainSliderCreated = true;
        if(topRatedCreated && popularCreated && mainSliderCreated) {

            ((HomeFragment) getParentFragment()).getView().findViewById(R.id.tabs).setVisibility(View.VISIBLE);
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) {
                ab.show();
            }
            ((LinearLayout) view.findViewById(R.id.tab_linear_layout)).setVisibility(View.VISIBLE);
            ((ProgressBar) view.findViewById(R.id.progress_bar)).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.loading_text_view)).setVisibility(View.GONE);
        }
    }

    private void createTopRated(View view) {
        //String mediaType = getArguments().getString("mediaType");
        String media = mediaType;
        if(media.equals("movie") || media.equals("tv")) {
            HashMap<String,String> params = new HashMap<String,String>();
            //params.put("media_type",media);
            makeRequest("/top-rated?media_type="+mediaType,new VolleyCallback() {
                @Override
                public void onSuccess(JSONArray items) {
                    setupTopRated(items,view);
                }
            });
        }

    }

    private void setupTopRated(JSONArray items,View view) {

        ArrayList<WatchListData> cardDataArrayList = new ArrayList<>();
        for(int i=0;i<items.length();i++) {
            String imgUrl = "https://cinemaone.net/images/movie_placeholder.png";
            try {
                imgUrl = items.getJSONObject(i).getString("poster_path");
            } catch (JSONException e) {}
            String title = "";
            try {
                title = items.getJSONObject(i).getString("title");
            } catch (JSONException e) {}
            try {
                String imgId = String.valueOf(items.getJSONObject(i).getInt("id"));
                cardDataArrayList.add(new WatchListData(title,imgUrl,imgId,mediaType));
            } catch (JSONException e) {
                continue;
            }
        }
        RecyclerView recyclerView = view.findViewById(R.id.rvTopRated);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        HorizontalCardsAdapter adapter = new HorizontalCardsAdapter(cardDataArrayList);
        recyclerView.setAdapter(adapter);
        topRatedCreated = true;
        if(topRatedCreated && popularCreated && mainSliderCreated) {
            ((HomeFragment) getParentFragment()).getView().findViewById(R.id.tabs).setVisibility(View.VISIBLE);
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) {
                ab.show();
            }
            ((LinearLayout) view.findViewById(R.id.tab_linear_layout)).setVisibility(View.VISIBLE);
            ((ProgressBar) view.findViewById(R.id.progress_bar)).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.loading_text_view)).setVisibility(View.GONE);

        }

    }

    private void createPopular(View view) {
        String media = mediaType;
        if(media.equals("movie") || media.equals("tv")) {
            HashMap<String,String> params = new HashMap<String,String>();
            makeRequest("/popular?media_type="+mediaType,new VolleyCallback() {
                @Override
                public void onSuccess(JSONArray items) {
                    setupPopular(items,view);
                }
            });
        }

    }

    private void setupPopular(JSONArray items,View view) {
        popularItems = items;
        ArrayList<WatchListData> cardDataArrayList = new ArrayList<>();
        for(int i=0;i<items.length();i++) {
            try {

                String imgUrl = items.getJSONObject(i).getString("poster_path");
                String imgId = String.valueOf(items.getJSONObject(i).getInt("id"));
                cardDataArrayList.add(new WatchListData(items.getJSONObject(i).getString("title"),imgUrl,imgId,mediaType));
            } catch (JSONException e) {
                continue;
            }
        }
        RecyclerView recyclerView = view.findViewById(R.id.rvPopular);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
        HorizontalCardsAdapter adapter = new HorizontalCardsAdapter(cardDataArrayList);
        recyclerView.setAdapter(adapter);
        popularCreated = true;
        if(topRatedCreated && popularCreated && mainSliderCreated) {

            ((HomeFragment) getParentFragment()).getView().findViewById(R.id.tabs).setVisibility(View.VISIBLE);
            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (ab != null) {
                ab.show();
            }
            ((LinearLayout) view.findViewById(R.id.tab_linear_layout)).setVisibility(View.VISIBLE);
            ((ProgressBar) view.findViewById(R.id.progress_bar)).setVisibility(View.GONE);
            ((TextView) view.findViewById(R.id.loading_text_view)).setVisibility(View.GONE);
        }
    }
}

