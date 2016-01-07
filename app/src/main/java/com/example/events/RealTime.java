package com.example.events;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by benjamin on 01/01/2016.
 */
public class RealTime extends AppCompatActivity implements View.OnClickListener
{

    private Button Event,RealTime,SavedEvent;
    private Toolbar toolbar2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_time);
        Event=(Button)findViewById(R.id.BarEvent_button);
        RealTime=(Button)findViewById(R.id.BarRealTime_button);
        SavedEvent=(Button)findViewById(R.id.BarSavedEvent_button);

        Event.setOnClickListener(this);
        SavedEvent.setOnClickListener (this);
        RealTime.setOnClickListener(this);

        RealTime.setTextColor (Color.WHITE);
        toolbar2 = (Toolbar) findViewById(R.id.toolbar_up);
        toolbar2.inflateMenu(R.menu.item);

    }

    @Override
    public void onClick(View v)
    {
        int vId=v.getId();
        Intent newIntent=null;
        if(vId==Event.getId())
        {
            newIntent=new Intent(this, MainActivity.class);
        }
        else if(vId==SavedEvent.getId())
        {
            newIntent=new Intent(this, com.example.events.SavedEvent.class);
        }
        if(vId!=RealTime.getId())startActivity(newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));


    }
    /**
     * Called when the user clicks the filter button
     */
    public void openFilterPage(MenuItem item) {
        Intent filterPageIntent = new Intent (this, FilterPage.class);
        startActivity (filterPageIntent);
    }

    public void city(MenuItem item) {
        ArrayList<String> list = new ArrayList<String>();

        String[] locales = Locale.getISOCountries();

        for (String countryCode : locales) {

            Locale obj = new Locale("", countryCode);

            System.out.println("Country Name = " + obj.getDisplayCountry());
            list.add(obj.getDisplayCountry());

        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Filter:
                openFilterPage(item);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }



}
