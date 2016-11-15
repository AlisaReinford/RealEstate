package com.mrtvrgn.mvrealestate.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;

import com.jude.rollviewpager.RollPagerView;
import com.mrtvrgn.mvrealestate.R;
import com.mrtvrgn.mvrealestate.datasets.Property;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsListAdapter extends RecyclerView.Adapter<SearchResultsListAdapter.ViewHolder> {

    public final static String TAG = SearchResultsListAdapter.class.getName();

    private List<Property> mDataSet = new ArrayList<>();

    private int mLastAnimatedItemPosition = -1;

    private Context context;

    public SearchResultsListAdapter(Context context) {
        this.context = context;
    }

    public interface OnItemClickListener {
        void onClick(Property property);
    }

    private OnItemClickListener mItemsOnClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mPropertyName;
        public final TextView mPropertyPrice;
        public final TextView mortgage;
        public final RollPagerView mPropertyImage;
        public final ImageView iv_on_sale;
        public final ImageView iv_on_rent;

        public ViewHolder(View view) {
            super(view);
            mPropertyName = (TextView) view.findViewById(R.id.tv_name);
            mPropertyPrice = (TextView) view.findViewById(R.id.tv_price);
            mortgage = (TextView) view.findViewById(R.id.tv_mortgage);
            mPropertyImage = (RollPagerView) view.findViewById(R.id.iv_property);
            iv_on_sale = (ImageView) view.findViewById(R.id.iv_on_sale);
            iv_on_rent = (ImageView) view.findViewById(R.id.iv_on_rent);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemsOnClickListener != null) {
                        mItemsOnClickListener.onClick(mDataSet.get(getAdapterPosition()));
                    }
                }

            });

        }
    }

    public void swapData(ArrayList<Property> mNewDataSet) {
        mDataSet = mNewDataSet;
        notifyDataSetChanged();
    }

    public void setItemsOnClickListener(OnItemClickListener onClickListener) {
        this.mItemsOnClickListener = onClickListener;
    }

    @Override
    public SearchResultsListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_results_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchResultsListAdapter.ViewHolder holder, final int position) {

        final Property property = mDataSet.get(position);
        holder.mPropertyName.setText(property.getP_address());
        if (Integer.parseInt(property.getO_on_rent()) == 1) {
            holder.mPropertyPrice.setText("$" + property.getP_price() + "/mo.");
        } else {
            if (Integer.parseInt(property.getP_on_sale()) == 1) {
                holder.mPropertyPrice.setText("$" + property.getP_price());
                holder.mortgage.setText("$" + property.getP_morgage() + "/mo.");
            }
        }
        RollViewAdapter rollViewAdapter = new RollViewAdapter(context);
        rollViewAdapter.setImgs(property.getP_image());
        holder.mPropertyImage.setAdapter(rollViewAdapter);

        if (property.getP_on_sale().equals("1")) {
            holder.iv_on_sale.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_green));
        } else {
            holder.iv_on_sale.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_red));
        }

        if (property.getO_on_rent().equals("1")) {
            holder.iv_on_rent.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_green));
        } else {
            holder.iv_on_rent.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_red));
        }

        if (mLastAnimatedItemPosition < position) {
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = position;
        }


    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        super.onViewRecycled(holder);
    }

    private void animateItem(View view) {
        view.setTranslationY(Util.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

}

