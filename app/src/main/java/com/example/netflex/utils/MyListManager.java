package com.example.netflex.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyListManager {
    private static final String PREF_NAME = "MyListPrefs";
    private static final String KEY_MY_LIST = "my_list";
    
    private SharedPreferences sharedPreferences;
    private Gson gson;
    
    public MyListManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }
    
    // Model class for My List items
    public static class MyListItem {
        public String id;
        public String title;
        public String poster;
        public String type; // "film" or "serie"
        public long addedTime;
        
        public MyListItem(String id, String title, String poster, String type) {
            this.id = id;
            this.title = title;
            this.poster = poster;
            this.type = type;
            this.addedTime = System.currentTimeMillis();
        }
    }
    
    // Add item to My List
    public void addToMyList(String id, String title, String poster, String type) {
        List<MyListItem> myList = getMyList();
        
        // Check if already exists
        for (MyListItem item : myList) {
            if (item.id.equals(id)) {
                return; // Already in list
            }
        }
        
        myList.add(new MyListItem(id, title, poster, type));
        saveMyList(myList);
    }
    
    // Remove item from My List
    public void removeFromMyList(String id) {
        List<MyListItem> myList = getMyList();
        myList.removeIf(item -> item.id.equals(id));
        saveMyList(myList);
    }
    
    // Check if item is in My List
    public boolean isInMyList(String id) {
        List<MyListItem> myList = getMyList();
        for (MyListItem item : myList) {
            if (item.id.equals(id)) {
                return true;
            }
        }
        return false;
    }
    
    // Get all My List items
    public List<MyListItem> getMyList() {
        String json = sharedPreferences.getString(KEY_MY_LIST, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        
        Type listType = new TypeToken<List<MyListItem>>(){}.getType();
        return gson.fromJson(json, listType);
    }
    
    // Save My List to SharedPreferences
    private void saveMyList(List<MyListItem> myList) {
        String json = gson.toJson(myList);
        sharedPreferences.edit()
            .putString(KEY_MY_LIST, json)
            .apply();
    }
    
    // Get My List count
    public int getMyListCount() {
        return getMyList().size();
    }
    
    // Get only series from My List
    public List<MyListItem> getMySeries() {
        List<MyListItem> myList = getMyList();
        List<MyListItem> series = new ArrayList<>();
        for (MyListItem item : myList) {
            if ("serie".equals(item.type)) {
                series.add(item);
            }
        }
        return series;
    }
    
    // Get only films from My List
    public List<MyListItem> getMyFilms() {
        List<MyListItem> myList = getMyList();
        List<MyListItem> films = new ArrayList<>();
        for (MyListItem item : myList) {
            if ("film".equals(item.type)) {
                films.add(item);
            }
        }
        return films;
    }
    
    // Get series count
    public int getMySeriesCount() {
        return getMySeries().size();
    }
    
    // Clear all My List
    public void clearMyList() {
        sharedPreferences.edit()
            .remove(KEY_MY_LIST)
            .apply();
    }
}
