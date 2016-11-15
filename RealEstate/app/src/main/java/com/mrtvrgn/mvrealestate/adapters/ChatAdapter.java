package com.mrtvrgn.mvrealestate.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mrtvrgn.mvrealestate.R;
import com.mrtvrgn.mvrealestate.datasets.ChatMessage;

import java.util.ArrayList;

/**
 * Purpose:
 * Related Classes:
 * Created by Mert Vurgun on 10/25/2016.
 */

public class ChatAdapter extends ArrayAdapter<ChatMessage> {
    // View lookup cache
    private static class ViewHolder {
        TextView smsBody;
        TextView smsDate;

    }


    public ChatAdapter(Context context, ArrayList<ChatMessage> data) {
        super(context, R.layout.list_item_chat, data);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        ChatMessage chatmsg = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_chat, parent, false);
            viewHolder.smsBody = (TextView) convertView.findViewById(R.id.textSMSBody);
            viewHolder.smsDate = (TextView) convertView.findViewById(R.id.text_sms_date);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data into the template view using the data object
        viewHolder.smsBody.setGravity(chatmsg.getSmsGravity());
        viewHolder.smsDate.setGravity(chatmsg.getSmsGravity());
        viewHolder.smsBody.setText(chatmsg.getSmsBody());
        viewHolder.smsDate.setText(chatmsg.getSmsDate());
        // Return the completed view to render on screen
        return convertView;
    }
}

