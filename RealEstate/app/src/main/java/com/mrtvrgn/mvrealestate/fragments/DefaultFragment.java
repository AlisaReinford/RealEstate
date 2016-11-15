package com.mrtvrgn.mvrealestate.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrtvrgn.mvrealestate.R;

/**
 * Purpose:
 * Related Classes:
 * Created by Mert Vurgun on 10/18/2016.
 */

public class DefaultFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_default, container, false);

        return v;
    }
}
