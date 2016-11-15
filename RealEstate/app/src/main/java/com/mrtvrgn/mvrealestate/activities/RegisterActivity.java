package com.mrtvrgn.mvrealestate.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.mrtvrgn.mvrealestate.R;
import com.mrtvrgn.mvrealestate.volley.VolleyAppController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText edittext_fname,
            edittext_lname,
            edittext_email,
            edittext_mobile,
            edittext_address,
            edittext_password;

    RadioButton type_seeker, type_provider;
    Button button_register;
    private String firstname, lastname, email, mobile, address ,passwd , type;
    private String SHAHash;
    public static int NO_OPTIONS=0;
    private boolean valid_control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edittext_fname = (EditText) findViewById(R.id.edittext_fname);
        edittext_lname = (EditText) findViewById(R.id.edittext_lname);
        edittext_email = (EditText) findViewById(R.id.edittext_email);
        edittext_mobile = (EditText) findViewById(R.id.edittext_mobile);
        edittext_address = (EditText) findViewById(R.id.edittext_address);
        edittext_password = (EditText) findViewById(R.id.edittext_password);
        type_seeker = (RadioButton) findViewById(R.id.radio_type_seeker);
        type_provider = (RadioButton) findViewById(R.id.radio_type_provider);
        valid_control = true;
        button_register = (Button)findViewById(R.id.button_register);
        type_seeker.setChecked(true);

        button_register.setOnClickListener(this);
        type_seeker.setOnClickListener(this);
        type_provider.setOnClickListener(this);

        //computeMD5Hash(passwd);

    } //end onCreate()

    private void registerUser(){

        setEditsEnable(false);
        String volley_tag = "register_request";

        if(type_seeker.isChecked())
            type = "SEEKER";
        else
            type = "PROVIDER";

        String url = String.valueOf("http://mertvurgun.x10host.com/mvestate/mvestateregister.php?" +
                "ufname=" + firstname +
                "&ulname=" + lastname +
                "&uemail=" + email +
                "&umobile=" + mobile +
                "&uaddress=" + address +
                "&utype=" + type +
                "&upass=" + computeSHAHash(passwd));

        url = url.replace(" ", "");

        StringRequest request = new StringRequest(Request.Method.GET, url , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("RegisterSuccess"))
                {
                    Toast.makeText(RegisterActivity.this, "Confirmation code has sent to your email, please verify before login.", Toast.LENGTH_LONG).show();
                    Intent intent_login = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent_login);
                }
                else
                    setEditsEnable(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                setEditsEnable(false);
            }
        });

        VolleyAppController.getmInstance().addToRequestQueue(request, volley_tag);
    }


    private void setEditsEnable(boolean val)
    {
        edittext_fname.setEnabled(val);
        edittext_lname.setEnabled(val);
        edittext_address.setEnabled(val);
        edittext_email.setEnabled(val);
        edittext_mobile.setEnabled(val);
        edittext_password.setEnabled(val);
    }


    private static String convertToHex(byte[] data) throws java.io.IOException
    {
        StringBuffer sb = new StringBuffer();
        String hex=null;

        hex= Base64.encodeToString(data, 0, data.length, NO_OPTIONS);

        sb.append(hex);

        return sb.toString();
    }

    public String computeSHAHash(String password)
    {
        MessageDigest mdSha1 = null;
        try
        {
            mdSha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e1) {
            Log.e("myapp", "Error initializing SHA1 message digest");
        }
        try {
            mdSha1.update(password.getBytes("ASCII"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        byte[] data = mdSha1.digest();
        try {
            SHAHash=convertToHex(data);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return SHAHash;
    }

    private void setDatas(){

        firstname = edittext_fname.getText().toString();
        if(firstname.isEmpty())
        {
            valid_control = false;
            Toast.makeText(this, "First Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
        lastname = edittext_lname.getText().toString();
        if(lastname.isEmpty())
        {
            valid_control = false;
            Toast.makeText(this, "Last name cannot be empty", Toast.LENGTH_SHORT).show();
            //Cant be empty or less than 3 chacaters
        }
        email = edittext_email.getText().toString();
        if(lastname.isEmpty())
        {
            valid_control = false;
            Toast.makeText(this, "Email Address cannot be empty", Toast.LENGTH_SHORT).show();
            //Cant be empty or must include @ and .
        }
        mobile = edittext_mobile.getText().toString();
        if(mobile.isEmpty())
        {
            valid_control = false;
            Toast.makeText(this, "Mobile number cannot be empty and need to be 10-11 numbers", Toast.LENGTH_SHORT).show();

            //Cant be empty or less than 11 chacaters
        }
        address = edittext_address.getText().toString();
        if(address.isEmpty())
        {
            valid_control = false;
            //Cant be empty or less than 20 chacaters
            Toast.makeText(this, "Address cannot be empty", Toast.LENGTH_SHORT).show();
        }
        passwd = edittext_password.getText().toString();
        if(passwd.isEmpty() || passwd.length() < 6)
        {
            valid_control = false;
            Toast.makeText(this, "Password at least 6 characters", Toast.LENGTH_SHORT).show();
            //Cant be empty or less than 6 chacaters
        }
    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.button_register:
                setDatas();
                if(valid_control)
                registerUser();
                valid_control = true;
                break;
            case R.id.radio_type_seeker:
                type_provider.setChecked(false);
                break;
            case R.id.radio_type_provider:
                type_seeker.setChecked(false);
                break;
        }
    }

    boolean approved = false;

    void confirmation(String memail, String code)
    {
        String confirm_tag = "confirm_tag";

        String url = "http://mertvurgun.x10host.com/mvestate/mvestateconfirmation.php?" +
                "uemail=" + memail +
                "&uconfirm=" + code;

        StringRequest confirm_request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("ConfirmSuccess")) {
                    approved = true;
                    Toast.makeText(RegisterActivity.this, "Account successfully activated, please Login.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else {
                    approved = false;
                    Toast.makeText(RegisterActivity.this, "Register is not successful. Please try again", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("RegisterConfirm", error.getMessage()   );
            }
        });

        VolleyAppController.getmInstance().addToRequestQueue(confirm_request, confirm_tag);
    }

    @Override
    protected void onStop() {
        super.onStop();
        setEditsEnable(true);
    }
}