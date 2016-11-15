package com.example.andro.realestate.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.arlib.floatingsearchview.util.Util;
import com.example.andro.realestate.app.AppController;
import com.example.andro.realestate.data.DataHelper;
import com.example.andro.realestate.data.Property;
import com.example.andro.realestate.R;
import com.example.andro.realestate.adapter.SearchResultsListAdapter;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import me.majiajie.pagerbottomtabstrip.Controller;
import me.majiajie.pagerbottomtabstrip.PagerBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.TabItemBuilder;
import me.majiajie.pagerbottomtabstrip.TabLayoutMode;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectListener;

public class SearchActivity extends AppCompatActivity {

    public final static String TAG = SearchActivity.class.getName();
    public final static String url = "http://www.rjtmobile.com/realestate/getproperty.php?all";

    int[] colors = {0xFF00796B,0xFF5B4947,0xFF607D8B,0xFFF57C00,0xFFF57C00};
    Controller controller;

    private ArrayList<Property> results = new ArrayList<>();

    FloatingSearchView mSearchView;
    NiftyDialogBuilder dialogBuilder;
    String filter = "Property Name...";
    SearchResultsListAdapter mAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    String query = url;
    DataHelper dataHelper = new DataHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initBottomTab();
        initView();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getResults(query);
                mAdapter.swapData(results);
            }
        };
        swipeRefreshLayout.setOnRefreshListener(listener);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        listener.onRefresh();
    }

    void initBottomTab() {
        PagerBottomTabLayout pagerBottomTabLayout = (PagerBottomTabLayout) findViewById(R.id.tab);

        TabItemBuilder tabItemBuilder = new TabItemBuilder(this).create()
                .setDefaultIcon(android.R.drawable.ic_menu_search)
                .setText("Search")
                .setSelectedColor(colors[0])
                .build();

        controller = pagerBottomTabLayout.builder()
                .addTabItem(tabItemBuilder)
                .addTabItem(android.R.drawable.ic_menu_compass, "Location",colors[1])
                .addTabItem(android.R.drawable.ic_menu_send, "Send",colors[2])
                .addTabItem(android.R.drawable.ic_menu_help, "Help",colors[3])
//                .setMode(TabLayoutMode.HIDE_TEXT)
//                .setMode(TabLayoutMode.CHANGE_BACKGROUND_COLOR)
                .setMode(TabLayoutMode.HIDE_TEXT| TabLayoutMode.CHANGE_BACKGROUND_COLOR)
                .build();

//        controller.setMessageNumber("A",2);
//        controller.setDisplayOval(0,true);

        controller.addTabItemClickListener(listener);
    }

    OnTabItemSelectListener listener = new OnTabItemSelectListener() {
        @Override
        public void onSelected(int index, Object tag)
        {
            Log.i("bottomTab","onSelected:"+index+"   TAG: "+tag.toString());

        }

        @Override
        public void onRepeatClick(int index, Object tag) {
            Log.i("bottomTab","onRepeatClick:"+index+"   TAG: "+tag.toString());
        }
    };


    void initView() {

        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.setSearchHint("Property Name...");

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {

            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.filter:
                        dialogBuilder = NiftyDialogBuilder.getInstance(SearchActivity.this);
                        dialogBuilder
                                .withTitle("Filter")
                                .withTitleColor("#FFFFFF")
                                .withDividerColor("#11000000")
                                .withDialogColor("#FFCCCCCC")
                                .withMessage(null)
                                .withMessageColor("#FFFFFFFF")
                                .withIcon(R.drawable.filter)
                                .setCustomView(R.layout.custom_view, SearchActivity.this);
                        final ListView listView = (ListView) dialogBuilder.findViewById(R.id.filter_list);
                        listView.setAdapter(new ArrayAdapter(
                                SearchActivity.this,
                                R.layout.filter_item,
                                R.id.item_text,
                                getResources().getStringArray(R.array.filter)));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                switch (position) {
                                    case 0:
                                        filter = "Property Name...";
                                        mSearchView.setSearchHint(filter);
                                        dialogBuilder.dismiss();
                                        break;
                                    case 1:
                                        filter = "Property Type...";
                                        mSearchView.setSearchHint(filter);
                                        dialogBuilder.dismiss();
                                        break;
                                    case 2:
                                        filter = "Property Location...";
                                        mSearchView.setSearchHint(filter);
                                        dialogBuilder.dismiss();
                                        break;
                                }
                            }
                        });
                        dialogBuilder.show();
                        break;
                    case R.id.setting:
                        break;
                }

            }
        });

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery

                //pass them on to the search view
                mSearchView.swapSuggestions(dataHelper.getSuggestion(newQuery));
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(SearchSuggestion searchSuggestion) {
                onSearchAction(searchSuggestion.getBody());
            }

            @Override
            public void onSearchAction(String currentQuery) {

                if(!currentQuery.equals(""))
                    dataHelper.saveToHistory(currentQuery);

                if (filter.equals("Property Name...")) {
                    String queruUrl = "http://www.rjtmobile.com/realestate/getproperty.php?psearch&pname=" + currentQuery + "&pptype=&ploc=&pcatid=";
                    query = queruUrl;
                    getResults(queruUrl);
                    mAdapter.swapData(results);
                    return;
                }
                if (filter.equals("Property Type...")) {
                    String queruUrl = "http://www.rjtmobile.com/realestate/getproperty.php?psearch&pname=&pptype=" + currentQuery + "&ploc=&pcatid=";
                    query = queruUrl;
                    getResults(queruUrl);
                    mAdapter.swapData(results);
                    return;
                }
                if (filter.equals("Property Location...")) {
                    String queruUrl = "http://www.rjtmobile.com/realestate/getproperty.php?psearch&pname=&pptype=&ploc=" + currentQuery + "&pcatid=";
                    query = queruUrl;
                    getResults(queruUrl);
                    mAdapter.swapData(results);
                    return;
                }

            }
        });

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                mSearchView.swapSuggestions(dataHelper.loadHistory());
            }

            @Override
            public void onFocusCleared() {

            }
        });

        mSearchView.setOnBindSuggestionCallback(new SearchSuggestionsAdapter.OnBindSuggestionCallback() {
            @Override
            public void onBindSuggestion(View suggestionView, ImageView leftIcon, TextView textView, SearchSuggestion item, int itemPosition) {
                leftIcon.setImageDrawable(ResourcesCompat.getDrawable(getResources(),
                        R.drawable.ic_history_black_24dp, null));
                leftIcon.setAlpha(.36f);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_results_list);
        mAdapter = new SearchResultsListAdapter(this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
    }


    void getResults(String url) {
        results.clear();
        // Tag used to cancel the request
        String tag_json_obj = "json_array_req";
        String query = url;
        JsonArrayRequest jsonObjReq = new JsonArrayRequest(
                query,
                new Response.Listener<JSONArray>() {

                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i("Lihang", response.toString());
                        Log.d(TAG, response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);

                                Property property = new Property();

                                property.setProperty_Id(object.getString("Property Id"));
                                property.setProperty_Name(object.getString("Property Name"));
                                property.setProperty_Type(object.getString("Property Category"));
                                property.setProperty_Address1(object.getString("Property Address1"));
                                property.setProperty_Address2(object.getString("Property Address2"));
                                property.setProperty_Zip(object.getString("Property Zip"));
                                property.setProperty_Image_1(object.getString("Property Image 1"));
                                property.setProperty_Image_2(object.getString("Property Image 2"));
                                property.setProperty_Image_3(object.getString("Property Image 3"));
                                property.setProperty_Latitude(object.getString("Property Latitude"));
                                property.setProperty_Longitude(object.getString("Property Longitude"));
                                property.setProperty_Cost(object.getString("Property Cost"));
                                property.setProperty_Size(object.getString("Property Size"));
                                property.setProperty_Desc(object.getString("Property Desc"));
                                property.setProperty_Published_Date(object.getString("Property Published Date"));
                                property.setProperty_Modify_Date(object.getString("Property Modify Date"));
                                property.setProperty_Status(object.getString("Property Status"));
                                property.setUser_Id(object.getString("User Id"));

                                results.add(property);
                            }

                        } catch (JSONException e) {
                            swipeRefreshLayout.setRefreshing(false);
                            e.printStackTrace();
                        }
                        mAdapter.swapData(results);
                        swipeRefreshLayout.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

}
