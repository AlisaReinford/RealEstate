package com.mrtvrgn.mvrealestate.activities;

import android.Manifest;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.SearchSuggestionsAdapter;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mrtvrgn.mvrealestate.R;
import com.mrtvrgn.mvrealestate.adapters.SearchResultsListAdapter;
import com.mrtvrgn.mvrealestate.datasets.DataHelper;
import com.mrtvrgn.mvrealestate.datasets.Property;
import com.mrtvrgn.mvrealestate.fragments.DisplayPropertyFragment;
import com.mrtvrgn.mvrealestate.fragments.MapListFragment;
import com.mrtvrgn.mvrealestate.fragments.SearchFragment;
import com.mrtvrgn.mvrealestate.fragments.SinglePropertyFragment;
import com.mrtvrgn.mvrealestate.volley.VolleyAppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.majiajie.pagerbottomtabstrip.Controller;
import me.majiajie.pagerbottomtabstrip.PagerBottomTabLayout;
import me.majiajie.pagerbottomtabstrip.TabItemBuilder;
import me.majiajie.pagerbottomtabstrip.TabLayoutMode;
import me.majiajie.pagerbottomtabstrip.listener.OnTabItemSelectListener;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class MainActivity extends AppCompatActivity implements SearchFragment.SetSearchAdapterListener, SearchResultsListAdapter.OnItemClickListener, SinglePropertyFragment.OnSinglePropertyFragmentInteracted, MapListFragment.onInfoBoxClicked, DisplayPropertyFragment.onPropertyItemClick {

    public final static String TAG = MainActivity.class.getName();
    public final static String url = "http://mertvurgun.x10host.com/mvestate/mvestategetproperties.php";

    public static int[] colors = {0xFF00796B, 0xFF5B4947, 0xFF607D8B, 0xFFF57C00, 0xFFF57C00};
    private Controller controller;
    private ArrayList<Property> results = new ArrayList<>();

    private FloatingSearchView mSearchView;
    private ImageView iv_no_result;
    private SwipeRefreshLayout swipeRefreshLayout;

    private String filter = "type";
    private String p_type = "";
    private String p_zip = "";
    private String p_num_bedroom = "";
    private String p_num_bath = "";
    private String p_num_car_allow = "";
    private String p_on_sale = "";
    private String p_on_rent = "";
    private String p_max_price = "";
    private String p_min_price = "";
    private String p_morgage = "";
    String query = url;
    DataHelper dataHelper = new DataHelper(this);
    PagerBottomTabLayout pagerBottomTabLayout;

    private SearchResultsListAdapter mAdapter;
    /*    private Fragment fragment;
        private SearchFragment searchFragment;*/
    private MapListFragment fragment;

    TourGuide mTourGuideHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_no_result = (ImageView) findViewById(R.id.iv_no_result);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        SwipeRefreshLayout.OnRefreshListener listener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                iv_no_result.setVisibility(View.INVISIBLE);
                getResults(query);
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

        /*Bottom Tab layout*/
        initBottomTab();
        /*Top Search Layout*/
        initSearchView();
    }

    void initBottomTab() {
        pagerBottomTabLayout = (PagerBottomTabLayout) findViewById(R.id.tab);

        TabItemBuilder tabItemBuilder = new TabItemBuilder(this).create()
                .setDefaultIcon(android.R.drawable.ic_menu_search)
                .setText("Search")
                .setSelectedColor(colors[0])
                .build();

        controller = pagerBottomTabLayout.builder()
                .addTabItem(tabItemBuilder)
                .addTabItem(android.R.drawable.ic_menu_compass, "Location", colors[1])
                .addTabItem(android.R.drawable.ic_menu_send, "Send", colors[2])
                .addTabItem(android.R.drawable.ic_menu_help, "Help", colors[3])
//                .setMode(TabLayoutMode.HIDE_TEXT)
//                .setMode(TabLayoutMode.CHANGE_BACKGROUND_COLOR)
                .setMode(TabLayoutMode.HIDE_TEXT | TabLayoutMode.CHANGE_BACKGROUND_COLOR)
                .build();

//        controller.setMessageNumber("A",2);
//        controller.setDisplayOval(0,true);

        controller.addTabItemClickListener(listener);
    }



    OnTabItemSelectListener listener = new OnTabItemSelectListener() {
        @Override
        public void onSelected(int index, Object tag) {
            mSearchView.setVisibility(View.VISIBLE);

            Log.i("bottomTab", "onSelected:" + index + "   TAG: " + tag.toString());
            switch (index) {
                case 0:
                    SearchFragment searchFragment = new SearchFragment();
                    searchFragment.setContext(MainActivity.this);
                    searchFragment.setListener(MainActivity.this, MainActivity.this);
                    getResults(query);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, searchFragment )
                            .commit();
                    break;
                case 1:
                    MapListFragment mapListFragment = new MapListFragment();
                    mapListFragment.setmPropertyList(results);
                    fragment = mapListFragment;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_container, mapListFragment)
                            .commit();
                    break;
                case 2:
                   DisplayPropertyFragment displayPropertyFragment = new DisplayPropertyFragment();
                    displayPropertyFragment.setmPropertyList(results);
                    //fragment = displayPropertyFragment;
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_container, displayPropertyFragment)
                            .commit();
                    break;
                case 3:
                    activateHelp();
                    break;
            }
        }

        @Override
        public void onRepeatClick(int index, Object tag) {
            Log.i("bottomTab", "onRepeatClick:" + index + "   TAG: " + tag.toString());
        }
    };


    void initSearchView() {


        mSearchView = (FloatingSearchView) findViewById(R.id.floating_search_view);
        mSearchView.setSearchHint("type");

        mSearchView.setOnMenuItemClickListener(new FloatingSearchView.OnMenuItemClickListener() {

            @Override
            public void onActionMenuItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.filter:
                        //inflate view
                        LayoutInflater inflater1 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View customView1 = inflater1.inflate(R.layout.custom_view_single, null);
                        final MaterialStyledDialog dialog1 = new MaterialStyledDialog.Builder(MainActivity.this)
                                .setStyle(Style.HEADER_WITH_TITLE)
                                .setHeaderDrawable(R.drawable.header)
                                .withDialogAnimation(true)
                                .withDarkerOverlay(true)
                                .setTitle("search by single")
                                .setCustomView(customView1)
                                .setPositiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeText("CANCEL")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {

                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .build();
                        ListView listView = (ListView) customView1.findViewById(R.id.filter_list);
                        listView.setAdapter(new ArrayAdapter(
                                MainActivity.this,
                                R.layout.filter_item,
                                R.id.item_text,
                                getResources().getStringArray(R.array.filter)));
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                switch (position) {
                                    case 0:
                                        filter = "type";
                                        mSearchView.setSearchHint(filter);
                                        mSearchView.clearQuery();
                                        dialog1.dismiss();
                                        break;
                                    case 1:
                                        filter = "zip code";
                                        mSearchView.setSearchHint(filter);
                                        mSearchView.clearQuery();
                                        dialog1.dismiss();
                                        break;
                                    case 2:
                                        filter = "number of bedroom";
                                        mSearchView.setSearchHint(filter);
                                        mSearchView.clearQuery();
                                        dialog1.dismiss();
                                        break;
                                    case 3:
                                        filter = "number of bathroom";
                                        mSearchView.setSearchHint(filter);
                                        mSearchView.clearQuery();
                                        dialog1.dismiss();
                                        break;
                                    case 4:
                                        filter = "number of parking lot";
                                        mSearchView.setSearchHint(filter);
                                        mSearchView.clearQuery();
                                        dialog1.dismiss();
                                        break;
                                    case 5:
                                        filter = "maximum price";
                                        mSearchView.setSearchHint(filter);
                                        mSearchView.clearQuery();
                                        dialog1.dismiss();
                                        break;
                                    case 6:
                                        filter = "minimum price";
                                        mSearchView.setSearchHint(filter);
                                        mSearchView.clearQuery();
                                        dialog1.dismiss();
                                        break;
                                    case 7:
                                        filter = "maximum mortgage";
                                        mSearchView.setSearchHint(filter);
                                        mSearchView.clearQuery();
                                        dialog1.dismiss();
                                        break;
                                }
                            }
                        });
                        dialog1.show();
                        break;
                    case R.id.all:
                        //inflate view
                        LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View customView = inflater2.inflate(R.layout.custom_view_all, null);
                        final MaterialStyledDialog dialog2 = new MaterialStyledDialog.Builder(MainActivity.this)
                                .setStyle(Style.HEADER_WITH_TITLE)
                                .setHeaderDrawable(R.drawable.header)
                                .withDialogAnimation(true)
                                .withDarkerOverlay(true)
                                .setTitle("search by all")
                                .setCustomView(customView)
                                .setPositiveText("OK")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        iv_no_result.setVisibility(View.INVISIBLE);
                                        EditText et_type = (EditText) customView.findViewById(R.id.et_type);
                                        p_type = et_type.getText().toString();
                                        EditText et_zip = (EditText) customView.findViewById(R.id.et_zip);
                                        p_zip = et_zip.getText().toString();
                                        EditText et_bedroom = (EditText) customView.findViewById(R.id.et_bedroom);
                                        p_num_bedroom = et_bedroom.getText().toString();
                                        EditText et_bath = (EditText) customView.findViewById(R.id.et_bath);
                                        p_num_bath = et_bath.getText().toString();
                                        EditText et_car = (EditText) customView.findViewById(R.id.et_car);
                                        p_num_car_allow = et_car.getText().toString();
                                        // p_on_sale =
                                        // p_on_rent =
                                        EditText et_max = (EditText) customView.findViewById(R.id.et_max_price);
                                        p_max_price = et_max.getText().toString();
                                        EditText et_min = (EditText) customView.findViewById(R.id.et_min_price);
                                        p_min_price = et_min.getText().toString();
                                        EditText et_mortgage = (EditText) customView.findViewById(R.id.et_max_morgage);
                                        p_morgage = et_mortgage.getText().toString();
                                        mSearchView.clearQuery();
                                        query = "http://mertvurgun.x10host.com/mvestate/mvestategetproperties.php?ptype=" + p_type + "&pzip=" + p_zip + "&pbed=" + p_num_bedroom + "&pbath=" + p_num_bath + "&pcar=" + p_num_car_allow + "&psate=&pminprice=" + p_min_price + "&pmaxprice=" + p_max_price + "&pmaxmorg=" + p_morgage;
                                        getResults(query);
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeText("CANCEL")
                                .onNegative(new MaterialDialog.SingleButtonCallback() {

                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .build();
                        dialog2.show();
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

                iv_no_result.setVisibility(View.INVISIBLE);

                if (!currentQuery.equals(""))
                    dataHelper.saveToHistory(currentQuery);

                if (filter.equals("type")) {
                    String queruUrl = "http://mertvurgun.x10host.com/mvestate/mvestategetproperties.php?ptype=" + currentQuery + "&pzip=&pbed=&pbath&pcar=&psate=&pminprice=&pmaxprice=&pmaxmorg=";
                    query = queruUrl;
                    getResults(queruUrl);
                    return;
                }
                if (filter.equals("zip code")) {
                    String queruUrl = "http://mertvurgun.x10host.com/mvestate/mvestategetproperties.php?ptype=&pzip=" + currentQuery + "&pbed=&pbath&pcar=&psate=&pminprice=&pmaxprice=&pmaxmorg=";
                    query = queruUrl;
                    getResults(queruUrl);
                    return;
                }
                if (filter.equals("number of bedroom")) {
                    String queruUrl = "http://mertvurgun.x10host.com/mvestate/mvestategetproperties.php?ptype=&pzip=&pbed=" + currentQuery + "&pbath&pcar=&psate=&pminprice=&pmaxprice=&pmaxmorg=";
                    query = queruUrl;
                    getResults(queruUrl);
                    return;
                }
                if (filter.equals("number of bathroom")) {
                    String queruUrl = "http://mertvurgun.x10host.com/mvestate/mvestategetproperties.php?ptype=&pzip=&pbed=&pbath=" + currentQuery + "&pcar=&psate=&pminprice=&pmaxprice=&pmaxmorg=";
                    query = queruUrl;
                    getResults(queruUrl);
                    return;
                }
                if (filter.equals("number of parking lot")) {
                    String queruUrl = "http://mertvurgun.x10host.com/mvestate/mvestategetproperties.php?ptype=&pzip=&pbed=&pbath&pcar=" + currentQuery + "&psate=&pminprice=&pmaxprice=&pmaxmorg=";
                    query = queruUrl;
                    getResults(queruUrl);
                    return;
                }
                if (filter.equals("maximum price")) {
                    String queruUrl = "http://mertvurgun.x10host.com/mvestate/mvestategetproperties.php?ptype=&pzip=&pbed=&pbath&pcar=&psate=&pminprice=&pmaxprice=" + currentQuery + "&pmaxmorg=";
                    query = queruUrl;
                    getResults(queruUrl);
                    return;
                }
                if (filter.equals("minimum price")) {
                    String queruUrl = "http://mertvurgun.x10host.com/mvestate/mvestategetproperties.php?ptype=&pzip=&pbed=&pbath&pcar=&psate=&pminprice=" + currentQuery + "&pmaxprice=&pmaxmorg=";
                    query = queruUrl;
                    getResults(queruUrl);
                    return;
                }
                if (filter.equals("maximum mortgage")) {
                    String queruUrl = "http://mertvurgun.x10host.com/mvestate/mvestategetproperties.php?ptype=&pzip=&pbed=&pbath&pcar=&psate=&pminprice=&pmaxprice=&pmaxmorg=" + currentQuery;
                    query = queruUrl;
                    getResults(queruUrl);
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


    }


    void getResults(String url) {
        results.clear();
        // Tag used to cancel the request
        String tag_json_obj = "json_object_req";
        String query = url;

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.GET,
                query,
                null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        /*Control Block - Commented for memory*/
                        //Log.i("Lihang", response.toString());
                        //Log.d(TAG, response.toString());
                        JSONArray jsonArray;
                        try {
                            jsonArray = response.getJSONArray("properties");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject object = jsonArray.getJSONObject(i);

                                Property property = new Property();

                                property.setProperty_id(object.getString("property_id"));
                                property.setP_address(object.getString("property_address"));
                                property.setP_type(object.getString("property_type"));
                                property.setP_image(object.getString("property_image"));
                                property.setP_num_bedroom(object.getString("number_of_bedroom"));
                                property.setP_num_bath(object.getString("number_of_bath"));
                                property.setP_num_car_allow(object.getString("number_of_car_allowance"));
                                property.setP_on_sale(object.getString("on_sale"));
                                property.setO_on_rent(object.getString("on_rent"));
                                property.setP_price(object.getString("property_price"));
                                property.setP_morgage(object.getString("morgage"));
                                property.setProv_name(object.getString("provider_name"));
                                property.setProv_mobile(object.getString("provider_mobile"));
                                property.setProv_image(object.getString("provider_image"));
                                property.setP_latitude(object.getString("latitude"));
                                property.setP_longitude(object.getString("longitude"));

                                results.add(property);
                            }

                        } catch (JSONException e) {
                            swipeRefreshLayout.setRefreshing(false);
                            e.printStackTrace();
                        }
                        if (mAdapter != null) {
                            mAdapter.swapData(results);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        if (fragment != null) {
                            fragment.showProperties(results);
                        }
                        if(results.size() == 0) {
                            iv_no_result.setVisibility(View.VISIBLE);
                        }

                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        swipeRefreshLayout.setRefreshing(false);
                        VolleyLog.d(TAG, "Error: " + error.getMessage());
                    }
                });
        VolleyAppController.getmInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    @Override
    public void setSearchAdapter(SearchResultsListAdapter searchAdapter) {
        mAdapter = searchAdapter;
    }

    @Override
    public void onClick(Property property) {
        mSearchView.setVisibility(View.INVISIBLE);
        pagerBottomTabLayout.setVisibility(View.INVISIBLE);
        SinglePropertyFragment singlePropertyFragment = new SinglePropertyFragment(property);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, singlePropertyFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void callRelevantPerson(String prov_mobile) {
        /*Most number in web server are real, 00 added for test purpose*/
        try {
            Intent call_internal = new Intent(Intent.ACTION_CALL, Uri.parse("tel:00" + prov_mobile));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            startActivity(call_internal);
        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),"Attempt is failed. Please try later or check permissions.",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void textToRelevantPerson(String prov_mobile , String message) {
        /*Most number in web server are real, 00 added for test purpose*/
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage("13312768472", null, message, null, null);
            Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();

            Intent chatActivityIntent = new Intent(MainActivity.this, ChatActivity.class);
            chatActivityIntent.putExtra("providerphone", prov_mobile);
            startActivity(chatActivityIntent);
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        mSearchView.setVisibility(View.VISIBLE);
        pagerBottomTabLayout.setVisibility(View.VISIBLE);
     /*   if(fragment instanceof MapListFragment)
            controller.setSelect(0);*/
        super.onBackPressed();
    }

    @Override
    public void proceedToSinglePropertyFragment(Property property) {
        SinglePropertyFragment singlePropertyFragment = new SinglePropertyFragment(property);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_container, singlePropertyFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void proceedToProperty(Property property) {
        onClick(property);
    }

    protected void set_tourguide(View view, String msg)
    {

            ToolTip toolTip = new ToolTip()
                    .setTitle("Property Displaying Types")
                    .setDescription(msg)
                    .setShadow(true)
                    .setGravity(Gravity.TOP);

            mTourGuideHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(toolTip)
                    .setOverlay(new Overlay());
            mTourGuideHandler.playOn(view);


    }

    private void activateHelp()
    {
        final Space spaces[] = new Space[]{(Space) findViewById(R.id.space_1),(Space) findViewById(R.id.space_2),(Space) findViewById(R.id.space_3),(Space) findViewById(R.id.space_4),(Space) findViewById(R.id.space_5)};
        final String informs[] = new String[]{"You can navigate through different display layouts from bottom..","You can navigate through different display layouts from bottom..","You can navigate through different display layouts from bottom..", "Filter you search result by single focus", "Filter your results with some combination of features"};
        new CountDownTimer(12500, 2500) {
            int counter = 0;
            boolean flag = false;
            public void onTick(long millisUntilFinished) {
                if(flag)
                {
                    mTourGuideHandler.cleanUp();
                }
                set_tourguide(spaces[counter], informs[counter++]);
                flag = true;
            }

            public void onFinish() {
                mTourGuideHandler.cleanUp();
                mTourGuideHandler = null;
            }
        }.start();
    }

}
