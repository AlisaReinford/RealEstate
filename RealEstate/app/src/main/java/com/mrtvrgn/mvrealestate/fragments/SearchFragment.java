package com.mrtvrgn.mvrealestate.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.mrtvrgn.mvrealestate.R;
import com.mrtvrgn.mvrealestate.adapters.SearchResultsListAdapter;
import com.mrtvrgn.mvrealestate.datasets.Property;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {

    private Context context;
    private ArrayList<Property> results = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private SearchResultsListAdapter searchAdapter;
    private SetSearchAdapterListener listener;
    private SearchResultsListAdapter.OnItemClickListener listener2;
    private ImageView iv_no_result;

    public interface SetSearchAdapterListener {
        void setSearchAdapter(SearchResultsListAdapter searchAdapter);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListener(SetSearchAdapterListener listener, SearchResultsListAdapter.OnItemClickListener listener2) {
        this.listener = listener;
        this.listener2 = listener2;
    }

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate( R.layout.fragment_search, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.search_results_list);
        searchAdapter = new SearchResultsListAdapter(context);
        searchAdapter.setItemsOnClickListener(listener2);
        listener.setSearchAdapter(searchAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(searchAdapter);
        return rootView;
    }

}
