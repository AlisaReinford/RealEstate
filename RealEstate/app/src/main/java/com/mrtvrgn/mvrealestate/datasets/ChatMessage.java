package com.mrtvrgn.mvrealestate.datasets;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Purpose:
 * Related Classes:
 * Created by Mert Vurgun on 10/25/2016.
 */

public class ChatMessage {

    private String smsBody;
    private long smsDate;
    private int smsGravity;


    public String getSmsBody() {
        return smsBody;
    }

    public void setSmsBody(String smsBody) {
        this.smsBody = smsBody;
    }

    public String getSmsDate() {
        Date date = new Date(smsDate);
        String formattedDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss").format(date);

        return formattedDate;
    }

    public void setSmsDate(long smsDate) {
        this.smsDate = smsDate;
    }

    public int getSmsGravity() {
        return smsGravity;
    }

    public void setSmsGravity(int smsGravity) {
        this.smsGravity = smsGravity;
    }
}
