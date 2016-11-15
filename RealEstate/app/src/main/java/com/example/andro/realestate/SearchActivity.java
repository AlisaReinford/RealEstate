package com.example.andro.realestate;

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
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener{

    public final static String TAG = SearchActivity.class.getName();
    public final static String url = "http://www.rjtmobile.com/realestate/getproperty.php?all";

    private ArrayList<Property> results = new ArrayList<>();

    FloatingSearchView mSearchView;
    NiftyDialogBuilder dialogBuilder;
    String filter = "Property Name";
    SearchResultsListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initView();
        getResults();
    }

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
                                .withMessage(null)
                                .withMessageColor("#FFFFFFFF")
                                .withIcon(R.drawable.filter)
                                .setCustomView(R.layout.custom_view,SearchActivity.this);
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

                ArrayList<String> list = new ArrayList<>();
                list.add("a");
                list.add("b");
                list.add("c");
                //pass them on to the search view
                //mSearchView.swapSuggestions(list);
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.search_results_list);
        mAdapter = new SearchResultsListAdapter();
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
    }

    void getResults() {
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
                            //pullRefreshLayout.setRefreshing(false);
                            e.printStackTrace();
                        }
                        mAdapter.swapData(results);
                        //pullRefreshLayout.setRefreshing(false);

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //pullRefreshLayout.setRefreshing(false);
                        Log.i("Lihang", error.getMessage());
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }


}
