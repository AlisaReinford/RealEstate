package com.example.andro.realestate;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.arlib.floatingsearchview.util.Util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsListAdapter extends RecyclerView.Adapter<SearchResultsListAdapter.ViewHolder> {

    public final static String TAG = SearchResultsListAdapter.class.getName();

    private List<Property> mDataSet = new ArrayList<>();

    private int mLastAnimatedItemPosition = -1;

    public interface OnItemClickListener {
        void onClick();
    }

    private OnItemClickListener mItemsOnClickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mPropertyName;
        public final TextView mPropertyPrice;
        public final ImageView mPropertyImage;

        public ViewHolder(View view) {
            super(view);
            mPropertyName = (TextView) view.findViewById(R.id.tv_name);
            mPropertyPrice = (TextView) view.findViewById(R.id.tv_price);
            mPropertyImage = (ImageView) view.findViewById(R.id.iv_property);
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
    public void onBindViewHolder(SearchResultsListAdapter.ViewHolder holder, final int position) {

        Property property = mDataSet.get(position);
        holder.mPropertyName.setText(property.getProperty_Name());
        holder.mPropertyPrice.setText(property.getProperty_Cost());
        imageLoading(property.getProperty_Image_1(), holder.mPropertyImage);


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

    private void animateItem(View view) {
        view.setTranslationY(Util.getScreenHeight((Activity) view.getContext()));
        view.animate()
                .translationY(0)
                .setInterpolator(new DecelerateInterpolator(3.f))
                .setDuration(700)
                .start();
    }

    private void imageLoading(String url, final ImageView imageView) {
        Log.i("alisa", url);
       /* try {

            URL l = new URL(url);
            URI i = new URI(l.getProtocol(), l.getUserInfo(), l.getHost(), l.getPort(), l.getPath(), l.getQuery(), l.getRef());
            l = i.toURL();*/
            ImageLoader imageLoader = AppController.getInstance().getmImageLoader();
            // If you are using normal ImageView
            imageLoader.get("http://" + url.replace('\\', '/'), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        imageView.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Image Load Error: " + error.getMessage());
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
        } /*catch (MalformedURLException e) {
            Log.i("alisa", "malformed");
            e.printStackTrace();
        } catch (URISyntaxException e) {
            Log.i("alisa", "urisyntax");
            e.printStackTrace();
        }
    }*/
}

