package com.example.andro.realestate.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by andro on 2016/10/23.
 */

public class DataHelper {

    public final static int limit = 3;
    private Context context;
    private ArrayList<PropertySuggestion>  sPropertySuggestions = new ArrayList<>();
    private SharedPreferences mSharedPreference;


    public DataHelper(Context context) {
        this.context = context;
    }


    public void saveToHistory(String newQuery) {
        loadHistory();
        if (sPropertySuggestions.size() == limit) {
            for (int i = 0; i < sPropertySuggestions.size(); i++) {
                if (sPropertySuggestions.get(i).getBody().equals(newQuery)) {
                    SharedPreferences.Editor mEdit = mSharedPreference.edit();
                    mEdit.remove(0 + "");
                    mEdit.putString(0 + "", newQuery);
                    mEdit.commit();
                    for (int j = i; j >= 1; j--) {
                        SharedPreferences.Editor mEdit2 = mSharedPreference.edit();
                        mEdit2.remove(j + "");
                        mEdit2.putString(j + "", sPropertySuggestions.get(j - 1).getBody());
                        mEdit2.commit();
                    }
                    return;
                }
            }
            SharedPreferences.Editor mEdit = mSharedPreference.edit();
            mEdit.remove(0 + "");
            mEdit.putString(0 + "", newQuery);
            mEdit.commit();
            for (int i = 1; i < sPropertySuggestions.size(); i++) {
                SharedPreferences.Editor mEdit2 = mSharedPreference.edit();
                mEdit2.remove(i + "");
                mEdit2.putString(i + "", sPropertySuggestions.get(i - 1).getBody());
                mEdit2.commit();
            }
        } else {
            for (int i = 0; i < sPropertySuggestions.size(); i++) {
                if (sPropertySuggestions.get(i).getBody().equals(newQuery)) {
                    SharedPreferences.Editor mEdit = mSharedPreference.edit();
                    mEdit.remove(0 + "");
                    mEdit.putString(0 + "", newQuery);
                    mEdit.commit();
                    for (int j = i; j >= 1; j--) {
                        SharedPreferences.Editor mEdit2 = mSharedPreference.edit();
                        mEdit2.remove(j + "");
                        mEdit2.putString(j + "", sPropertySuggestions.get(j - 1).getBody());
                        mEdit2.commit();
                    }
                    return;
                }
            }
            SharedPreferences.Editor mEdit = mSharedPreference.edit();
            mEdit.remove(0 + "");
            mEdit.putString(0 + "", newQuery);
            mEdit.commit();
            for (int i = 1; i < sPropertySuggestions.size() + 1; i++) {
                SharedPreferences.Editor mEdit2 = mSharedPreference.edit();
                mEdit2.remove(i + "");
                mEdit2.putString(i + "", sPropertySuggestions.get(i - 1).getBody());
                mEdit2.commit();
            }
        }
    }

    public ArrayList<PropertySuggestion> loadHistory() {

        mSharedPreference = context.getSharedPreferences("SearchHistory", Context.MODE_PRIVATE);
        if (mSharedPreference == null) {
            return sPropertySuggestions;
        }
        sPropertySuggestions.clear();
        Map<String, String> map =  (Map<String, String>) mSharedPreference.getAll();
        TreeMap<Integer, String> treeMap = new TreeMap<>();
        for (String s : map.keySet()) {
            treeMap.put(Integer.parseInt(s), map.get(s));
        }
        for (Integer i : treeMap.keySet()) {
            PropertySuggestion ps = new PropertySuggestion(treeMap.get(i));
            sPropertySuggestions.add(ps);
        }
        return sPropertySuggestions;

    }

    public ArrayList<PropertySuggestion> getSuggestion(String constraint) {
        loadHistory();
        ArrayList<PropertySuggestion> results = new ArrayList<>();
        for (PropertySuggestion suggestion : sPropertySuggestions) {
            if (suggestion.getBody().toUpperCase()
                    .startsWith(constraint.toString().toUpperCase())) {

                results.add(suggestion);
            }
        }
        return results;
    }
}
