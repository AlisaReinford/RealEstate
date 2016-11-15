package com.mrtvrgn.mvrealestate.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mrtvrgn.mvrealestate.R;


/**
 * Created by kobe.gong on 2015/7/17.
 */
public class LoginFormView extends LinearLayout {

    private EditText edit1, edit2;

    public LoginFormView(Context context) {
        super(context);
        loadView();
    }

    public LoginFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadView();
    }

    public LoginFormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        loadView();
    }

    private void loadView(){
        setOrientation(VERTICAL);
        LayoutInflater.from(getContext()).inflate(R.layout.dynamic_login_layout, this);
        edit1 = (EditText) findViewById(R.id.edit_login_email);
        edit2 = (EditText) findViewById(R.id.edit_login_password);
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        edit1.setFocusable(focusable);
        edit2.setFocusable(focusable);
    }
}
