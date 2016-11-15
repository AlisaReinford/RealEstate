package com.mrtvrgn.mvrealestate.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mrtvrgn.mvrealestate.R;
import com.mrtvrgn.mvrealestate.datasets.Property;
import com.mrtvrgn.mvrealestate.fragments.DisplayPropertyFragment;
import com.mrtvrgn.mvrealestate.volley.VolleyAppController;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Purpose:
 * Related Classes:
 * Created by Mert Vurgun on 10/18/2016.
 */

public class DisplayPropertyAdapter extends RecyclerView.Adapter<DisplayPropertyAdapter.cViewHolder>{

    /*Constant Tag for class*/
    private static final String TAG = DisplayPropertyAdapter.class.getSimpleName();

    Context mContext;
    ArrayList<Property> mPropertyList;
    OnItemClickListener mItemClickListener;

    public DisplayPropertyAdapter(Context context, ArrayList<Property> data){
        mContext = context;
        mPropertyList = data;
    }

    @Override
    public cViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View custom_p_card_view = LayoutInflater.from(mContext).inflate(R.layout.card_single_property, parent, false);
        return new cViewHolder(custom_p_card_view);
    }

    @Override
    public void onBindViewHolder(cViewHolder holder, int position) {
        Property temp_property = mPropertyList.get(position);
        holder.image_property.setImageUrl(temp_property.getP_image()[0], holder.mImageLoader);
        if(temp_property.getStatus().equals("ON RENT")) {
            holder.text_p_statu.setTextColor(Color.GREEN);
            holder.text_p_price.setText(temp_property.getP_price());
        }
        else {
            holder.text_p_statu.setTextColor(Color.RED);
            if(!temp_property.getP_morgage().equals("0"))
            holder.text_p_price.setText(temp_property.getP_price() + " - $" + temp_property.getP_morgage() + "/mon");
            else
                holder.text_p_price.setText(temp_property.getP_price());
        }
        holder.text_p_statu.setText(temp_property.getStatus());

        holder.text_p_address.setText(temp_property.getP_address());
        holder.text_p_info.setText(temp_property.getP_num_bedroom() + " Beds " + temp_property.getP_num_bath() + " Baths " + temp_property.getP_num_car_allow() + " Cars Allowed");

    }

    @Override
    public int getItemCount() {
        return mPropertyList.size();
    }

    public class cViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        NetworkImageView image_property;
        TextView text_p_address, text_p_price, text_p_statu, text_p_info;
        ImageLoader mImageLoader;
        ImageView image_hide_card, image_like_card, image_favorite;

        public cViewHolder(final View itemView) {
            super(itemView);

            image_property = (NetworkImageView) itemView.findViewById(R.id.image_card_property);
            text_p_address = (TextView) itemView.findViewById(R.id.text_card_address);
            text_p_price = (TextView) itemView.findViewById(R.id.text_card_price);
            text_p_statu = (TextView)itemView.findViewById(R.id.text_card_status);
            text_p_info = (TextView)itemView.findViewById(R.id.text_card_property_info);

            mImageLoader = VolleyAppController.getmInstance().getImageLoader();

            image_hide_card = (ImageView) itemView.findViewById(R.id.image_card_hide);
            image_like_card = (ImageView) itemView.findViewById(R.id.image_card_favorite);

            image_hide_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Use to remove the item from recycler view and

                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
                    alertDialog.setMessage("Would you like to hide this property?");
                    alertDialog.setTitle("Remove Permission");
                    alertDialog.setCancelable(true);
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeAt(getAdapterPosition());
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
                }
            });

            image_like_card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Use to provide liked/favorite list
                    image_favorite = (ImageView) v.findViewById(R.id.image_card_favorite);
                    image_favorite.setImageResource(R.drawable.red_fav_heart);
                }
            });

            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getPosition());
            }
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void removeAt(int position) {
        mPropertyList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mPropertyList.size());
    }
}
