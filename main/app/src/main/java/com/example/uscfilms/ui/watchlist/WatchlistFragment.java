package com.example.uscfilms.ui.watchlist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uscfilms.ItemMoveCallback;
import com.example.uscfilms.OnBackPressedListener;
import com.example.uscfilms.R;
import com.example.uscfilms.WatchListAdapter;
import com.example.uscfilms.WatchListData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;

public class WatchlistFragment extends Fragment implements OnBackPressedListener {

    private WatchlistViewModel watchlistViewModel;
    RecyclerView recyclerView;
    WatchListAdapter adapter;
    ArrayList<WatchListData> watchListDataArrayList = new ArrayList<>();
    View view;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }
        watchlistViewModel =
                new ViewModelProvider(this).get(WatchlistViewModel.class);
        View root = inflater.inflate(R.layout.fragment_watchlist, container, false);
        view = root;
        recyclerView = root.findViewById(R.id.rvWatchList);
        recyclerView.setLayoutManager(new GridLayoutManager(root.getContext(),3));
        //populateRecyclerView();
        return root;
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }*/


    @Override
    public void onBackPressed() {
        populateRecyclerView();
    }

    @Override
    public void onViewCreated( View view, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onResume() {
        populateRecyclerView();
        super.onResume();
    }
    public void populateRecyclerView() {
        if (view != null) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
            String currWatchlist = pref.getString("watchlist", null);
            Type watchlistType = new TypeToken<ArrayList<WatchListData>>() {
            }.getType();
            TextView noWatchListText = view.findViewById(R.id.no_watchlist_text);
            final Gson gson = new Gson();
            watchListDataArrayList = gson.fromJson(currWatchlist, watchlistType);
            adapter = new WatchListAdapter(watchListDataArrayList, noWatchListText);
            ItemTouchHelper.Callback callback = new ItemMoveCallback(adapter);
            ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
            touchHelper.attachToRecyclerView(recyclerView);
            recyclerView.setAdapter(adapter);
            if (currWatchlist == null || currWatchlist.equals("[]")) {
                noWatchListText.setVisibility(View.VISIBLE);
            } else {
                noWatchListText.setVisibility(View.GONE);
            }
        }
    }
}