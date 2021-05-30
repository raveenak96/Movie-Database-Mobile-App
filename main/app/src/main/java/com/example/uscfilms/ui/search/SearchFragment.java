package com.example.uscfilms.ui.search;

import android.app.Notification;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.uscfilms.Globals;
import com.example.uscfilms.R;
import com.example.uscfilms.SearchResultsAdapter;
import com.example.uscfilms.SearchResultsData;
import com.example.uscfilms.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private RequestQueue queue;
    public String backendBase;
    public SearchResultsAdapter adapter;
    public ArrayList<SearchResultsData> resultsDataArrayList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }
        searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);

        View root = inflater.inflate(R.layout.fragment_search, container, false);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        backendBase = Globals.getInstance().getBackend();
        queue = queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        final SearchView searchView = (SearchView) view.findViewById(R.id.search_view);
        searchView.setQueryHint("Search movies and TV");
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(false);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                TextView noResults = view.findViewById(R.id.no_results_view);
                noResults.setVisibility(View.GONE);
                if (newText.length() > 0) {
                    createSearchResults(newText, view);
                }
                /*else {
                    if(resultsDataArrayList!=null && !resultsDataArrayList.isEmpty()) {
                        resultsDataArrayList.clear();
                        adapter.notifyDataSetChanged();
                    }
            }*/
                return true;
            }
        });
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

    public void createSearchResults(String query,View view) {
        makeArrayRequest("/search?query="+query,new VolleyCallback() {
            @Override
            public void onSuccess(JSONArray results) {
                setupSearchResults(results,view);
            }
        });
    }

    public void setupSearchResults(JSONArray results,View view) {
        TextView noResults = view.findViewById(R.id.no_results_view);
        if(results.length()==0) {
            noResults.setVisibility(View.VISIBLE);
            if(resultsDataArrayList!=null && !resultsDataArrayList.isEmpty()) {
                resultsDataArrayList.clear();
                adapter.notifyDataSetChanged();
            }

        } else {
            noResults.setVisibility(View.INVISIBLE);
            resultsDataArrayList = new ArrayList<>();
            for(int i=0;i<results.length();i++) {
                try {
                    JSONObject resultItem = results.getJSONObject(i);
                    String imgUrl = resultItem.getString("backdrop_path");
                    String id = String.valueOf(resultItem.getInt("id"));
                    String mediaType = resultItem.getString("media_type");
                    String title = resultItem.getString("title");
                    Double rating = resultItem.getDouble("vote_average");
                    String date = "";
                    if(mediaType.equals("movie")) {
                        date = resultItem.getString("release_date");
                    } else {
                        date = resultItem.getString("first_air_date");
                    }
                    resultsDataArrayList.add(new SearchResultsData(imgUrl,id,title,mediaType,rating,date));
                } catch (JSONException e) {
                    continue;
                }
            }
            RecyclerView recyclerView = view.findViewById(R.id.rvSearchResults);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext(),LinearLayoutManager.VERTICAL,false));
            adapter = new SearchResultsAdapter(resultsDataArrayList);
            recyclerView.setAdapter(adapter);

        }
    }
}