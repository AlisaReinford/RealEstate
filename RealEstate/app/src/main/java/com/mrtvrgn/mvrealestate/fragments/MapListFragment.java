package com.mrtvrgn.mvrealestate.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.mrtvrgn.mvrealestate.R;
import com.mrtvrgn.mvrealestate.datasets.Property;
import com.mrtvrgn.mvrealestate.volley.VolleyAppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

/**
 * Created by MV-HP-ProBook on 10/24/2016.
 */

public class MapListFragment extends Fragment{

    private static final String TAG = MapListFragment.class.getSimpleName();
    /*Map View - Full Screen*/
    private MapView mMapView;
    /*Google Map - Map and its items control*/
    private GoogleMap mGoogleMap;

    /*Base context of MapListFragment: MainActivity*/
    Context mContext;
    /*Property List: can be either populated with full property list or a search result could be settled*/
    ArrayList<Property> mPropertyList;
    /*Collection Map object for each Map Marker(ID) to recognize Property*/
    private Map<String, Property> markedPropertyMap;
    /*Fragment Interface callback MainActivity-MapListFragment communication*/
    onInfoBoxClicked mCallBackMLF;

    private TextView property_count;
    private int number_of_properties;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        /*Initializing Collection Map Structure*/
        markedPropertyMap = new HashMap<String, Property>();
        /*Initializing callback of interface*/
        mCallBackMLF = (onInfoBoxClicked) getActivity();

    }

    /*public accessed property list*/
    public void setmPropertyList(ArrayList<Property> mPropertyList) {
        this.mPropertyList = mPropertyList;
        number_of_properties = mPropertyList.size();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view_full_map = inflater.inflate(R.layout.fragment_maplist, container, false);

        /*Initializing TextView on map: displays number of properties marked*/
        property_count = (TextView)view_full_map.findViewById(R.id.text_property_count);

        mMapView = (MapView) view_full_map.findViewById(R.id.map_view_full);
        mMapView.onCreate(savedInstanceState);
        if (mMapView != null) {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;

                    //Adding marker to map
                    if(mPropertyList.size()<=0)
                        getProperties();
                    else
                        showProperties(mPropertyList);
                    //Moving the camera
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(41.672255, -87.792778)));
                    //Animating the camera
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(9));

                    mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {

                            //Log.d(TAG, markedPropertyMap.get(marker.getId()).getP_price() );
                            /*InfoWindows clicked we need to forward fragment to single property details*/
                            mCallBackMLF.proceedToSinglePropertyFragment(markedPropertyMap.get(marker.getId()));
                        }
                    });

                }
            });
        }

        property_count.setText(number_of_properties + " properties found");

        return  view_full_map;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            try {
                mMapView.onDestroy();
            } catch (NullPointerException e) {
                Log.d(TAG, "Error while attempting MapView.onDestroy(), ignoring exception", e);
            }
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    public void showProperties(ArrayList<Property> properties) {
        mGoogleMap.clear();
        add_markers(properties);
        property_count.setText(properties.size() + " properties found");
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

                        mPropertyList.add(singleProperty);
                    }
                    number_of_properties = mPropertyList.size();
                    add_markers(mPropertyList);
                    /*add markers*/
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

    private void add_markers(ArrayList<Property> list)
    {
        /*Clear the Map<Key,Property> collection before marking the map*/
        markedPropertyMap.clear();
        /*Put marker for each item that result of search*/
        for (int counter = 0; counter < list.size(); counter++) {

            Marker newMark = mGoogleMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(list.get(counter).getO_on_rent().equals("1") ? R.drawable.house_blue_102 : R.drawable.house_red_102))
                    .position(new LatLng(list.get(counter).getP_latitude(), list.get(counter).getP_longitude())) //setting position
                    .draggable(true) //Making the marker draggable
                    .title("$" + list.get(counter).getP_price())); //Adding a title

            markedPropertyMap.put(newMark.getId(), list.get(counter));

        }
    }

    public interface onInfoBoxClicked
    {
        public void proceedToSinglePropertyFragment(Property property);
    }
}

