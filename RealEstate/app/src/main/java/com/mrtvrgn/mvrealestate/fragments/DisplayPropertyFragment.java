package com.mrtvrgn.mvrealestate.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mrtvrgn.mvrealestate.R;
import com.mrtvrgn.mvrealestate.adapters.DisplayPropertyAdapter;
import com.mrtvrgn.mvrealestate.datasets.Property;
import com.mrtvrgn.mvrealestate.volley.VolleyAppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Purpose: Displaying a property list within given criteria
 * Related Classes:
 * Created by Mert Vurgun on 10/18/2016.
 */
@SuppressLint("ValidFragment")
public class DisplayPropertyFragment extends Fragment {

    /*Constant Tag for class*/
    private static final String TAG = DisplayPropertyFragment.class.getSimpleName();
    /*Base context of DisplayPropertyFragment*/
    Context mContext;
    /**/
    ArrayList<Property> mPropertyList;
    /*RecyclerView*/
    RecyclerView mRecyclerView;
    RecyclerView.LayoutManager mLayoutManager;
    DisplayPropertyAdapter mDisplayPropertyAdapter;
    //Interface object
    onPropertyItemClick mCallBackProperty;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mPropertyList = new ArrayList<Property>();

        //Instantiating the Activity's interface object
        mCallBackProperty = (onPropertyItemClick) getActivity();
    }

    /*public accessed property list*/
    public void setmPropertyList(ArrayList<Property> mPropertyList) {
        this.mPropertyList = mPropertyList;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);

        View view_custom_propery_fragment = inflater.inflate(R.layout.fragment_properties, container, false);

        mRecyclerView = (RecyclerView) view_custom_propery_fragment.findViewById(R.id.recycler_view_property);               //Find the related recycler view

        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);                                   //Load layout manager, Grid is ont of the default provided from recycler view

        mRecyclerView.setLayoutManager(mLayoutManager);                                             //Set Layout Manager to RecyclerView object

        if(mPropertyList.size()<=0)
        getProperties();
        else
        mRecyclerView.setAdapter(mDisplayPropertyAdapter);                                                              //Download elements, parse and load to product list
        //Set Adapter after elements loaded

        mDisplayPropertyAdapter = new DisplayPropertyAdapter(mContext, mPropertyList);                             //Instantiate adapter with loaded data


        //Notify data changes on RecyclerView's Adapter. This may not be needed in many case
        mDisplayPropertyAdapter.notifyDataSetChanged();


        //RecyclerView item click listener
        mDisplayPropertyAdapter.SetOnItemClickListener(new DisplayPropertyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //Push to Activity method with clicked item ID
                mCallBackProperty.proceedToProperty(mPropertyList.get(position));
            }
        });

        return view_custom_propery_fragment;
    }

    public void getProperties()
    {
        final String p_request_tag = "p_request_tag_j1";
        String p_url = "http://mertvurgun.x10host.com/realestate_responser.php";
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.GET, p_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray properties = response.getJSONArray("properties");

                    for (int i = 0; i < properties.length(); i++){
                        Property singleProperty = new Property();
                        JSONObject itemProperty = (JSONObject)properties.get(i);
                        singleProperty.setProperty_id(itemProperty.getString("property_id"));
                        singleProperty.setP_address(itemProperty.getString("property_address"));
                        singleProperty.setP_image(itemProperty.getString("property_image"));
                        singleProperty.setP_type(itemProperty.getString("property_type"));
                        singleProperty.setP_num_bedroom(itemProperty.getString("number_of_bedroom"));
                        singleProperty.setP_num_bath(itemProperty.getString("number_of_bath"));
                        singleProperty.setP_num_car_allow(itemProperty.getString("number_of_car_allowance"));
                        singleProperty.setO_on_rent(itemProperty.getString("on_sale"));
                        singleProperty.setO_on_rent(itemProperty.getString("on_rent"));
                        singleProperty.setP_price(itemProperty.getString("property_price"));
                        singleProperty.setP_morgage(itemProperty.getString("morgage"));
                        singleProperty.setProv_name(itemProperty.getString("provider_name"));
                        singleProperty.setProv_mobile(itemProperty.getString("provider_mobile"));
                        singleProperty.setProv_image(itemProperty.getString("provider_image"));
                        singleProperty.setP_latitude(itemProperty.getString("latitude"));
                        singleProperty.setP_longitude(itemProperty.getString("longitude"));
                        //Fill the category list by real data
                        mPropertyList.add(singleProperty);
                    }

                    mRecyclerView.setAdapter(mDisplayPropertyAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error);
            }
        });


        VolleyAppController.getmInstance().addToRequestQueue(jsonArrayRequest, p_request_tag);
    }

    //Fragment-Category Connection Interface
    public interface onPropertyItemClick{

        void proceedToProperty(Property property);
    }
}
