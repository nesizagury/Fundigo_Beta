package com.example.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by benjamin on 01/01/2016.
 */
public class Menu extends AppCompatActivity {
    private final static String TAG = "Menu";
    LoginButton facebook_login_button;
    CallbackManager callbackManager;
    Button sms_login_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_menu);

        facebook_login_button = (LoginButton) findViewById (R.id.login_button11);
        sms_login_button = (Button) findViewById(R.id.button3);
        String number = readFromFile ();
        if(!number.isEmpty ()){
            sms_login_button.setText ("You logged in as " + number);
            sms_login_button.setOnClickListener (null);
        }
        AccessToken accessToken = AccessToken.getCurrentAccessToken ();
        if (accessToken != null) {
            facebook_login_button.setVisibility (View.INVISIBLE);
        }

        callbackManager = CallbackManager.Factory.create ();
        facebook_login_button.registerCallback (callbackManager, new FacebookCallback<LoginResult> () {
            @Override
            public void onSuccess(LoginResult loginResult) {

            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText (getApplicationContext (), "error", Toast.LENGTH_SHORT).show ();
            }
        });
    }

    private String readFromFile() {
        String phone_number = "";
        try {
            InputStream inputStream = openFileInput ("verify.txt");
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
                BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
                String receiveString = "";
                while ((receiveString = bufferedReader.readLine ()) != null) {
                    phone_number = receiveString;
                }
                inputStream.close ();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        return phone_number;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        facebook_login_button.setVisibility (View.INVISIBLE);
        callbackManager.onActivityResult (requestCode, resultCode, data);
    }

    public void smsLogin(View view) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (Menu.this, SmsSignUpActivity.class);
        startActivity (intent);
    }

    @Override
    protected void onResume() {
        super.onResume ();
        String number = readFromFile ();
        if(!number.isEmpty ()){
            sms_login_button.setText ("You logged in as " + number);
            sms_login_button.setOnClickListener (null);
        }
    }
}
