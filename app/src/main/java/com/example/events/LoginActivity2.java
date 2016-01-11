package com.example.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity2 extends Activity {

    Button loginButton;
    Button signupButton;
    String username;
    String password;
    EditText usernameET;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        this.requestWindowFeature (Window.FEATURE_NO_TITLE);
        setContentView (R.layout.activity_login2);

        usernameET = (EditText) findViewById (R.id.username_et);
        passwordET = (EditText) findViewById (R.id.password_et);

        loginButton = (Button) findViewById (R.id.button_login);
        signupButton = (Button) findViewById (R.id.button_signup);


    }

    public void Login(View v) {
        username = usernameET.getText ().toString ();
        password = passwordET.getText ().toString ();

        List<ParseUser> list = new ArrayList<ParseUser> ();
        ParseQuery<ParseUser> query1 = ParseUser.getQuery ();
        try {
            list = query1.find ();
        } catch (ParseException e) {
            Toast.makeText (this, "Error " + e, Toast.LENGTH_SHORT).show ();
        }
        boolean exists = false;
        for (ParseUser user : list) {
            if (user.getUsername ().equals (username)) {
                exists = true;
                break;
            }
        }
        if (exists) {
            try {
                ParseUser.logIn (username, password);
                Toast.makeText (getApplicationContext (), "Successfully Loged in", Toast.LENGTH_SHORT).show ();
                Intent intent = new Intent (this, MainActivity.class);
                startActivity (intent);
                finish ();
            } catch (ParseException e1) {
                Toast.makeText (getApplicationContext (), "Wrong Password, try again :)", Toast.LENGTH_SHORT).show ();
            }
        } else {
            Toast.makeText (getApplicationContext (), "This user does not exist, try again :)", Toast.LENGTH_SHORT).show ();
        }
    }


    public void Signup(View view) {
        username = usernameET.getText ().toString ();
        password = passwordET.getText ().toString ();
        List<ParseUser> list = new ArrayList<ParseUser> ();
        ParseQuery<ParseUser> query1 = ParseUser.getQuery ();
        try {
            list = query1.find ();
        } catch (ParseException e) {
            Toast.makeText (this, "Error " + e, Toast.LENGTH_SHORT).show ();
        }
        boolean exists = false;
        for (ParseUser user : list) {
            if (user.getUsername ().equals (username)) {
                exists = true;
                break;
            }
        }
        if (exists) {
            Toast.makeText (getApplicationContext (), "Username exists, try again :)", Toast.LENGTH_SHORT).show ();
        } else {
            ParseUser user = new ParseUser ();
            user.setUsername (username);
            user.setPassword (password);
            user.isNew ();
            try {
                user.signUp ();
                Toast.makeText (getApplicationContext (), "Successfully Signed up", Toast.LENGTH_SHORT).show ();
                Intent intent = new Intent (this, MainActivity.class);
                startActivity (intent);
                finish ();
            } catch (ParseException e) {
                Toast.makeText (getApplicationContext (), "Error" + e, Toast.LENGTH_SHORT).show ();
            }
        }
    }
}
