package com.example.uscfilms;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class WatchListHelper {

    Type watchlistType = new TypeToken<ArrayList<WatchListData>>() {}.getType();
    Gson gson = new Gson();
    public WatchListHelper() {}

    public ArrayList<WatchListData> getCurrWatchlist(Context context) {
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0); // 0 - for private mode
        String currWatchlist = pref.getString("watchlist",null);
        if(currWatchlist!=null) {
            return gson.fromJson(currWatchlist,watchlistType);
        } else {
            return new ArrayList<>();
        }
    }

    public boolean isInWatchlist(Context context, String itemId) {
        ArrayList<WatchListData> currWatchList = getCurrWatchlist(context);
        for(int i=0;i<currWatchList.size();i++) {
            WatchListData item = currWatchList.get(i);
            if(item.getImgId().equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    public void addItem(Context context,WatchListData newItem) {
        ArrayList<WatchListData> currWatchList = getCurrWatchlist(context);
        currWatchList.add(newItem);
        String newWatchList = gson.toJson(currWatchList);
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.putString("watchlist",newWatchList);
        editor.commit();
    }

    public ArrayList<WatchListData> removeItem(Context context, String itemId) {
        ArrayList<WatchListData> currWatchList = getCurrWatchlist(context);
        currWatchList.removeIf(item -> item.getImgId().equals(itemId));
        String newWatchList = gson.toJson(currWatchList);
        SharedPreferences pref = context.getSharedPreferences("MyPref", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.putString("watchlist",newWatchList);
        editor.commit();
        return currWatchList;
    }
}
