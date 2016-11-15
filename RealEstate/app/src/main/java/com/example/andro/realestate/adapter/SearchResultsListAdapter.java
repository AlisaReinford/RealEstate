package com.example.andro.realestate.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.Util;
import com.example.andro.realestate.R;
import com.example.andro.realestate.data.Property;
import com.jude.rollviewpager.RollPagerView;

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
        void onClick();
    }

    private OnItemClickListener mItemsOnClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mPropertyName;
        public final TextView mPropertyPrice;
        public final RollPagerView mPropertyImage;

        public ViewHolder(View view) {
            super(view);
            mPropertyName = (TextView) view.findViewById(R.id.tv_name);
            mPropertyPrice = (TextView) view.findViewById(R.id.tv_price);
            mPropertyImage = (RollPagerView) view.findViewById(R.id.iv_property);

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

        Property property = mDataSet.get(position);
        holder.mPropertyName.setText(property.getProperty_Name());
        holder.mPropertyPrice.setText("$ " + property.getProperty_Cost());
        RollViewAdapter rollViewAdapter = new RollViewAdapter();
        rollViewAdapter.setImgs(property.getProperty_Image_1(), property.getProperty_Image_2(), property.getProperty_Image_3());
        holder.mPropertyImage.setAdapter(rollViewAdapter);


        /*try {
            URL l = new URL("http://" + property.getProperty_Image_1().replace('\\', '/'));
            URI i = new URI(l.getProtocol(), l.getUserInfo(), l.getHost(), l.getPort(), l.getPath(), l.getQuery(), l.getRef());
            l = i.toURL();
            Picasso.with(context).load(l.toString()).into(holder.mPropertyImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/


        if (mLastAnimatedItemPosition < position) {
            animateItem(holder.itemView);
            mLastAnimatedItemPosition = position;
        }

        if (mItemsOnClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mItemsOnClickListener.onClick();
                }
            });
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

