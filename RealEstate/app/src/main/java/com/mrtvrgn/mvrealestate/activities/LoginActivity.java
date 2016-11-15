package com.mrtvrgn.mvrealestate.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mrtvrgn.mvrealestate.R;
import com.mrtvrgn.mvrealestate.datasets.EncyrptionSHA1;
import com.mrtvrgn.mvrealestate.utils.LoginFormView;
import com.mrtvrgn.mvrealestate.volley.VolleyAppController;
import com.victor.loading.rotate.RotateLoading;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String VIDEO_NAME = "weomcomevid.mp4";

    private static final String TAG = LoginActivity.class.getSimpleName();
    EditText edit_email, edit_pass;
    Button btn_signin, btn_goreg;

    String email_login, password_login;

    private VideoView mVideoView;

    private InputType inputType = InputType.NONE;

    private Button buttonLeft, buttonRight;

    private LoginFormView loginFormView;

    private TextView appName;

    private ViewGroup contianer;

    private RotateLoading rotateLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseMessaging.getInstance().subscribeToTopic("HomieTest");
        FirebaseInstanceId.getInstance().getToken();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            window.setFlags(
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_main_test);
        getSupportActionBar().hide();

        findView();

        initView();

        File videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists()) {
            videoFile = copyVideoFile();
        }

        playVideo(videoFile);

        playAnim();
    }


    private void findView() {
        rotateLoading = (RotateLoading) findViewById(R.id.rotateloading);
        mVideoView = (VideoView) findViewById(R.id.videoView);
        buttonLeft = (Button) findViewById(R.id.buttonLeft);
        buttonRight = (Button) findViewById(R.id.buttonRight);
        contianer = (ViewGroup) findViewById(R.id.container);
        loginFormView = (LoginFormView) findViewById(R.id.formView);

        appName = (TextView) findViewById(R.id.appName);
        loginFormView.post(new Runnable() {
            @Override
            public void run() {
                int delta = loginFormView.getTop()+ loginFormView.getHeight();
                loginFormView.setTranslationY(-1 * delta);
            }
        });
    }

    private void initView() {

        buttonRight.setOnClickListener(this);
        buttonLeft.setOnClickListener(this);
    }

    private void playVideo(File videoFile) {
        mVideoView.setVideoPath(videoFile.getPath());
        mVideoView.setLayoutParams(new RelativeLayout.LayoutParams(-1, -1));
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        });
    }

    private void playAnim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(appName, "alpha", 0,1);
        anim.setDuration(4000);
        anim.setRepeatCount(1);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                appName.setVisibility(View.INVISIBLE);
            }
        });
    }

    @NonNull
    private File copyVideoFile() {
        File videoFile;
        try {
            FileOutputStream fos = openFileOutput(VIDEO_NAME, MODE_PRIVATE);
            InputStream in = getResources().openRawResource(R.raw.welcomevid);
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff)) != -1) {
                fos.write(buff, 0, len);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        videoFile = getFileStreamPath(VIDEO_NAME);
        if (!videoFile.exists())
            throw new RuntimeException("video file has problem, are you sure you have welcome_video.mp4 in res/raw folder?");
        return videoFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

    @Override
    public void onClick(View view) {
        int delta = loginFormView.getTop()+ loginFormView.getHeight();

        switch (inputType) {
            case NONE:

                if (view == buttonLeft) {
                    loginFormView.animate().translationY(0).alpha(1).setDuration(500).start();
                    inputType = InputType.LOGIN;
                    buttonLeft.setText(R.string.button_confirm_login);
                    buttonRight.setText(R.string.button_cancel_login);
                } else if (view == buttonRight) {
                    inputType = InputType.SIGN_UP;
                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                }

                break;
            case LOGIN:

                loginFormView.animate().translationY(-1 * delta).alpha(0).setDuration(500).start();

                if (view == buttonLeft) {

                } else if (view == buttonRight) {

                }
                inputType = InputType.NONE;
                buttonLeft.setText(R.string.button_login);
                //EditText ed = (EditText) loginFormView.findViewById(R.id.edit1);
                //Toast.makeText(this, ed.getText().toString(), Toast.LENGTH_SHORT).show();
                EditText mEmail = (EditText) loginFormView.findViewById(R.id.edit_login_email);
                EditText mPass = (EditText) loginFormView.findViewById(R.id.edit_login_password);
                verify_login(mEmail.getText().toString(), mPass.getText().toString());
                buttonRight.setText(R.string.button_signup);
                break;
            case SIGN_UP:

                loginFormView.animate().translationY(-1 * delta).alpha(0).setDuration(500).start();
                if (view == buttonLeft) {

                } else if (view == buttonRight) {

                }
                inputType = InputType.NONE;
                buttonLeft.setText(R.string.button_login);
                buttonRight.setText(R.string.button_signup);
                break;

        }
    }

    enum InputType {
        NONE, LOGIN, SIGN_UP;
    }

    private void verify_login(final String _email, String _pass){
        rotateLoading.start();
        String login_tag = "volley_login";

        EncyrptionSHA1 pass_enctyp = new EncyrptionSHA1(_pass);
        String url = "http://mertvurgun.x10host.com/mvestate/mvestatelogin.php?" +
                "uemail=" + _email +
                "&upass=" + pass_enctyp.getPassword();

        StringRequest user_request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("LoginNotActive"))
                {
                    buildDialog(R.style.DialogAnimation_2,"Up-Down Animation!", _email);
                }
                else if (response.equals("LoginFail")){
                    //Email-or-password mismatch in db, please check
                    AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                    alertDialog.setTitle("Account not found");
                    alertDialog.setMessage("Would you like to create a new account ?");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                    rotateLoading.stop();
                }
                else
                {
                    rotateLoading.stop();
                    //Login Successfull
                    Toast.makeText(LoginActivity.this, "Login Successfull", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, error.getMessage());
            }
        });

        VolleyAppController.getmInstance().addToRequestQueue(user_request, login_tag);
    }

    private void buildDialog(int animationSource, String type, final String memail) {
        EditText edit_code = null;
        final android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);
        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();

        View dialogLayout = inflater.inflate(R.layout.dialog_confirm, null);
        alertDialog.setView(dialogLayout);
        alertDialog.setCancelable(false);
        edit_code = (EditText)dialogLayout.findViewById(R.id.edittext_confirm);
        alertDialog.setTitle("Activate Account");
        // Add action buttons
        final EditText finalEdit_code = edit_code;
        alertDialog.setPositiveButton(R.string.button_confirm_login, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                confirmation(memail, finalEdit_code.getText().toString());
            }
        });
        alertDialog.setNegativeButton(R.string.button_cancel_signup, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        android.support.v7.app.AlertDialog dialog = alertDialog.create();

        dialog.getWindow().getAttributes().windowAnimations = animationSource;
        dialog.show();
    }

    void confirmation(String email_, String code_)
    {
        rotateLoading.stop();
        String confirm_tag = "confirm_tag";

        String url = "http://mertvurgun.x10host.com/mvestate/mvestateconfirmation.php?" +
                "uemail=" + email_ +
                "&uconfirm=" + code_;

        StringRequest confirm_request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(response.equals("ConfirmSuccess")) {
                    //approved = true;
                    Toast.makeText(LoginActivity.this, "Account successfully activated, please Login.", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(LoginActivity.this, "Confirmation Failed, please try again.", Toast.LENGTH_LONG).show();
                    //approved = false;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("RegisterConfirm", error.getMessage()   );
            }
        });

        VolleyAppController.getmInstance().addToRequestQueue(confirm_request, confirm_tag);
    }
}
