package com.mrtvrgn.mvrealestate.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jude.rollviewpager.adapter.StaticPagerAdapter;
import com.mrtvrgn.mvrealestate.R;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by andro on 2016/10/22.
 */

public class RollViewAdapter extends StaticPagerAdapter {

    public final static String TAG = RollViewAdapter.class.getName();

    private String[] imgs;

    private Context context;



    public RollViewAdapter(Context context) {
        this.context = context;
    }

    public void setImgs(String[] imgs) {
        this.imgs = imgs;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        if (imgs[position].equals("")) {
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
            view.setImageDrawable(container.getResources().getDrawable(R.drawable.no_image));
        } else {
            // imageLoading(imgs[position], view);
               /*try {
            URL l = new URL("http://" + imgs[position].replace('\\', '/'));
            URI i = new URI(l.getProtocol(), l.getUserInfo(), l.getHost(), l.getPort(), l.getPath(), l.getQuery(), l.getRef());
            l = i.toURL();
            Picasso.with(context).load(l.toString()).into(holder.mPropertyImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/
            try {
                URL l = new URL(imgs[position]);
                URI i = new URI(l.getProtocol(), l.getUserInfo(), l.getHost(), l.getPort(), l.getPath(), l.getQuery(), l.getRef());
                l = i.toURL();
                Glide
                        .with(context)
                        .load(l.toString())
                        .centerCrop()
                        .crossFade()
                        .into(view);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return view;
    }

    @Override
    public int getCount() {
        return imgs.length;
    }

  /*  private void imageLoading(String url, final ImageView imageView) {

        final String mUrl = url;
        try {
            URL l = new URL("http://" + mUrl.replace('\\', '/'));
            URI i = new URI(l.getProtocol(), l.getUserInfo(), l.getHost(), l.getPort(), l.getPath(), l.getQuery(), l.getRef());
            l = i.toURL();
            ImageLoader imageLoader = AppController.getInstance().getmImageLoader();
            // If you are using normal ImageView
            imageLoader.get(l.toString(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setImageBitmap(response.getBitmap());
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Image Load Error: " + error.getMessage());
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }*/
}
