package com.example.events;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.parse.Parse;
import com.parse.ParseObject;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Parse.enableLocalDatastore (this);
        Parse.initialize (this);
        ParseObject.registerSubclass (Event.class);
        Intent intent = new Intent (this, MainActivity.class);
        startActivity(intent);
        this.finish ();
    }
}
