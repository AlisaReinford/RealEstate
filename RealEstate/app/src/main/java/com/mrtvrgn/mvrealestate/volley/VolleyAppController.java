package com.mrtvrgn.mvrealestate.volley;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Purpose: Creating a Singleton class for Volley Library
 * Related Classes: Application
 * Created by Mert Vurgun on 10/9/2016.
 */

public class VolleyAppController extends Application {

    public static final String TAG = VolleyAppController.class.getSimpleName();

    //Create RequestQueue object from Volley
    private RequestQueue mRequestQueue;
    //Create ImageLoader object from Volley
    private ImageLoader mImageLoader;
    //Create class's instance to ensure being single
    private static VolleyAppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
    //Returns the current and single instance of this singleton class
    public static synchronized VolleyAppController getmInstance(){
        return mInstance;
    }
    //Returns the RequestQueue object
    //If the object is not created, create new instance
    public RequestQueue getmRequestQueue(){
        if(mRequestQueue==null)
        {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    /**
     * Adding new request to request queue of Volley
     * @param request: Incoming request
     * @param tag: Request's tag
     * @param <T>
     */
    public <T> void addToRequestQueue(Request<T> request, String tag)
    {
        //Set defaul TAG if the is empty
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        //Add the new request to request queue
        getmRequestQueue().add(request);
    }

    public  void cancelPendingRequest(Object tag)
    {
        //If the request queue is not empty, cancel the tagged request
        if(mRequestQueue != null)
            mRequestQueue.cancelAll(tag);
    }

    public ImageLoader getImageLoader() {
        getmRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.mRequestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }
}
