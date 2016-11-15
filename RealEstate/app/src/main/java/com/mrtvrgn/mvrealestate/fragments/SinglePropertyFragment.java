package com.mrtvrgn.mvrealestate.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;


import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jude.rollviewpager.RollPagerView;
import com.mrtvrgn.mvrealestate.R;
import com.mrtvrgn.mvrealestate.adapters.RollViewAdapter;
import com.mrtvrgn.mvrealestate.datasets.Property;
import com.mrtvrgn.mvrealestate.volley.VolleyAppController;
import com.ramotion.foldingcell.FoldingCell;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;


/**
 * Purpose:
 * Related Classes:
 * Created by Mert Vurgun on 10/19/2016.
 */

@SuppressLint("ValidFragment")
public class SinglePropertyFragment extends Fragment {

    private static final String TAG = SinglePropertyFragment.class.getSimpleName();

    Property mProperty;
    Context mContext;
    MapView mMapView;
    GoogleMap googleMap;
    GoogleApiClient mGoogleApiClient;
    ImageLoader mImageLoader;
    LayoutInflater mLayoutInflator;

    //To store longitude and latitude from map
    private double longitude;
    private double latitude;

    Geocoder mGeocoder;

    OnSinglePropertyFragmentInteracted mCallBackSingleProp;

    public SinglePropertyFragment(Property property)
    {
        mProperty = property;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Setting the base context
        mContext = context;
        //Setting Geocoder for forward geocoding
        mGeocoder = new Geocoder(context);
        //Apply forward geocoding to get Latitude and Longitude
        longitude = mProperty.getP_longitude();
        latitude = mProperty.getP_latitude();

        mImageLoader = VolleyAppController.getmInstance().getImageLoader();

        mCallBackSingleProp = (OnSinglePropertyFragmentInteracted) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view_single_property = inflater.inflate(R.layout.fragment_single_property, container, false);
        mLayoutInflator = inflater;
        RollPagerView rollPagerView = (RollPagerView) view_single_property.findViewById(R.id.iv_single_property);
        RollViewAdapter rollViewAdapter = new RollViewAdapter(mContext);
        rollViewAdapter.setImgs(mProperty.getP_image());
        rollPagerView.setAdapter(rollViewAdapter);

        init(view_single_property);

        mMapView = (MapView) view_single_property.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        if (mMapView != null) {
            mMapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {

                    googleMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(mProperty.getO_on_rent().equals("1") ? R.drawable.house_blue_102 : R.drawable.house_red_102))
                            .position(new LatLng(latitude, longitude)) //setting position
                            .draggable(true) //Making the marker draggable
                            .title(mProperty.getP_address())); //Adding a title

                    //Moving the camera
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));
                    //Animating the camera
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));

                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {

                            // Create a Uri from an intent string. Use the result to create an Intent.
                            Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+latitude+","+longitude);

// Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
// Make the Intent explicit by setting the Google Maps package
                            mapIntent.setPackage("com.google.android.apps.maps");

// Attempt to start an activity that can handle the Intent
                            mContext.startActivity(mapIntent);
                        }
                    });
                }
            });
        }

        return view_single_property;
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

    public void init(View view)
    {
        TextView text_p_price,text_p_address, text_p_bed, text_p_bath, text_p_car, text_prov_name1, text_prov_mobile1, text_prov_name2, text_prov_mobile2;
        NetworkImageView image_prov_prof1, image_prov_prof2;

        image_prov_prof1 = (NetworkImageView) view.findViewById(R.id.image_relevant_profile1);
        image_prov_prof2 = (NetworkImageView) view.findViewById(R.id.image_relevant_profile2);

        image_prov_prof1.setImageUrl(mProperty.getProv_image(), mImageLoader);
        image_prov_prof2.setImageUrl(mProperty.getProv_image(), mImageLoader);

        text_p_price = (TextView) view.findViewById(R.id.text_fsp_property_price);
        text_p_address = (TextView) view.findViewById(R.id.text_fsp_property_address);
        text_p_bed = (TextView) view.findViewById(R.id.text_fsp_property_beds);
        text_p_bath = (TextView) view.findViewById(R.id.text_fsp_property_baths);
        text_p_car = (TextView) view.findViewById(R.id.text_fsp_property_cars);
        text_prov_name1 = (TextView) view.findViewById(R.id.text_relevant_p_name1);
        text_prov_name2 = (TextView) view.findViewById(R.id.text_relevant_p_name2);
        text_prov_mobile1 = (TextView) view.findViewById(R.id.text_relevant_p_mobile1);
        text_prov_mobile2 = (TextView) view.findViewById(R.id.text_relevant_p_mobile2);

        if(!mProperty.getO_on_rent().equals("1"))
        {
            if(mProperty.getP_morgage().equals("0"))
                text_p_price.setText("$" + mProperty.getP_price());
            else
                text_p_price.setText("$" + mProperty.getP_price() + "    -    " + "Morgage: $" + mProperty.getP_morgage() + "/mon");
        }
        else
        {
            text_p_price.setText("$" + mProperty.getP_price() + "/mon");
        }

        text_p_address.setText(mProperty.getP_address());
        text_p_bed.setText(getString(R.string.beds) + ":" + mProperty.getP_num_bedroom());
        text_p_bath.setText(getString(R.string.baths) + ":" + mProperty.getP_num_bath());
        text_p_car.setText(getString(R.string.cars)+ ":" + mProperty.getP_num_car_allow());
        text_prov_name1.setText(mProperty.getProv_name());
        text_prov_name2.setText(mProperty.getProv_name());
        text_prov_mobile1.setText(mProperty.getProv_mobile());
        text_prov_mobile2.setText(mProperty.getProv_mobile());


        // get our folding cell
        final FoldingCell fc = (FoldingCell) view.findViewById(R.id.folding_cell);

        // attach click listener to folding cell
        fc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fc.toggle(false);
            }
        });

        // attach click listener to fold btn
        final Button foldBtn = (Button) view.findViewById(R.id.button_contact_now);
        foldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   mCallBackSingleProp.callRelevantPerson(mProperty.getProv_mobile());
            }
        });

        // attach click listener to toast btn
        final Button toastBtn = (Button) view.findViewById(R.id.button_send_message);
        toastBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mCallBackSingleProp.textToRelevantPerson(mProperty.getProv_mobile());
                buildDialog(R.style.DialogAnimation_2,"Up-Down Animation!", mProperty.getProv_mobile());
               }
        });
    }


    public interface OnSinglePropertyFragmentInteracted{

        void callRelevantPerson(String mobile_no);

        void textToRelevantPerson(String prov_mobile, String body);
    }

    private void buildDialog(int animationSource, String type, final String phone) {
        EditText edit_code = null;
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext);
        // Get the layout inflater
        LayoutInflater inflater = mLayoutInflator;

        View dialogLayout = inflater.inflate(R.layout.dialog_compose_sms, null);
        alertDialog.setView(dialogLayout);
        alertDialog.setCancelable(false);
        edit_code = (EditText)dialogLayout.findViewById(R.id.edit_message_body);
        alertDialog.setTitle("Compose SMS");
        // Add action buttons
        final EditText finalEdit_code = edit_code;
        alertDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                mCallBackSingleProp.textToRelevantPerson(phone, finalEdit_code.getText().toString());
            }
        });
        alertDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        android.support.v7.app.AlertDialog dialog = alertDialog.create();

        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();
    }
}
