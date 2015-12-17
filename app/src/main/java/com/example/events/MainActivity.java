package com.example.events;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    ListView list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list_view = (ListView) findViewById(R.id.listView);
        Adapters adapts = new Adapters(this);
        list_view.setAdapter(adapts);
    }

    /** Called when the user clicks the filter button */
    public void openFilterPage(View view)
    {
        Intent filterPageIntent = new Intent(this, FilterPage.class);
        startActivity(filterPageIntent);
    }
}
