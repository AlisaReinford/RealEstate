package com.mrtvrgn.mvrealestate.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.Telephony;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mrtvrgn.mvrealestate.R;
import com.mrtvrgn.mvrealestate.adapters.ChatAdapter;
import com.mrtvrgn.mvrealestate.datasets.ChatMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{

    String relevant_contact;
    ListView listView ;
    EditText edit_message;
    Button button_send;
    ArrayList<ChatMessage> lstSms;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        relevant_contact = getIntent().getStringExtra("providerphone");

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        getSupportActionBar().setTitle("+1("+relevant_contact.substring(0,3) + ")" + relevant_contact.substring(3,7) + "-" + relevant_contact.substring(7)); // set the top title
        String title = actionBar.getTitle().toString(); // get the title
        //actionBar.hide(); // or even hide the actionbar



        edit_message = (EditText) findViewById(R.id.edit_chat_message);
        button_send = (Button) findViewById(R.id.button_send_chat_message);
        button_send.setOnClickListener(this);

        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        lstSms = new ArrayList<ChatMessage>();
        ContentResolver cr = getContentResolver();

        Cursor c = cr.query(Telephony.Sms.Sent.CONTENT_URI, // Official CONTENT_URI from docs
                new String[]{Telephony.Sms.Sent.ADDRESS, Telephony.Sms.Sent.BODY, Telephony.Sms.Sent.DATE}, // Select body text
                null,
                null,
                Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order

        int totalSMS = c.getCount();

        if (c.moveToFirst()) {

            for (int i = 0; i < totalSMS; i++) {

                if (c.getString(0).equals("+1" + "3213052901")) {
                    ChatMessage msd = new ChatMessage();
                    msd.setSmsGravity(Gravity.RIGHT);
                    msd.setSmsBody(c.getString(1));
                    msd.setSmsDate(c.getLong(2));
                    lstSms.add(msd);
                }

                c.moveToNext();
            }

            c = cr.query(Telephony.Sms.Inbox.CONTENT_URI, // Official CONTENT_URI from docs
                    new String[]{Telephony.Sms.Inbox.ADDRESS, Telephony.Sms.Inbox.BODY, Telephony.Sms.Inbox.DATE}, // Select body text
                    null,
                    null,
                    Telephony.Sms.Inbox.DEFAULT_SORT_ORDER); // Default sort order

            if (c.moveToFirst()) {

                for (int i = 0; i < totalSMS; i++) {

                    if (c.getString(0).equals("+1" + "3213052901")) {
                        ChatMessage msd = new ChatMessage();
                        msd.setSmsGravity(Gravity.LEFT);
                        msd.setSmsBody(c.getString(1));
                        msd.setSmsDate(c.getLong(2));
                        lstSms.add(msd);
                    }

                    c.moveToNext();
                }
            } else {
                throw new RuntimeException("You have no SMS in Inbox");
            }
            c.close();

            Collections.sort(lstSms, new Comparator<ChatMessage>() {
                public int compare(ChatMessage one, ChatMessage other) {
                    return one.getSmsDate().compareTo(other.getSmsDate());
                }
            });

            chatAdapter = new ChatAdapter(ChatActivity.this, lstSms);

            listView.setAdapter(chatAdapter);
            chatAdapter.notifyDataSetChanged();


        }
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {

        String message ;

        if(v.getId() == R.id.button_send_chat_message)
        {
            message = edit_message.getText().toString();

            if(message.isEmpty())
                Toast.makeText(this, "Message cannot be empty!", Toast.LENGTH_SHORT).show();
            else
            {
                /*Most number in web server are real, 00 added for test purpose*/
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    //smsManager.sendTextMessage(relevant_contact, null, message, null, null);//Original
                    smsManager.sendTextMessage("13312768472", null, message, null, null);//Test

                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSmsBody(message);
                    chatMessage.setSmsDate(new Date().getTime());
                    chatMessage.setSmsGravity(Gravity.RIGHT);//ME

                    lstSms.add(chatMessage);

                    chatAdapter.notifyDataSetChanged();

                    edit_message.setText("");

                    Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                }

                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
    }
}
