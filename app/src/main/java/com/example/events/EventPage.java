package com.example.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class EventPage extends Activity {

    private TextView myText = null;
    private String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_event_page);
        Intent intent = getIntent ();
        int image_id = intent.getIntExtra ("eventImage", R.mipmap.pic0);
        ImageView event_image = (ImageView) findViewById (R.id.eventPage_image);
        event_image.setImageResource (image_id);
        String date = intent.getStringExtra ("eventDate");
        TextView event_date = (TextView) findViewById (R.id.eventPage_date);
        event_date.setText (date);
        String eventName = intent.getStringExtra ("eventName");
        TextView event_name = (TextView) findViewById (R.id.eventPage_name);
        event_name.setText (eventName);
        String eventTags = intent.getStringExtra ("eventTags");
        TextView event_tags = (TextView) findViewById (R.id.eventPage_tags);
        event_tags.setText (eventTags);
        String eventPrice = intent.getStringExtra ("eventPrice");
        TextView event_price = (TextView) findViewById (R.id.priceEventPage);
        event_price.setText (eventPrice);
        String eventInfo = intent.getStringExtra ("eventInfo");
        TextView event_info = (TextView) findViewById (R.id.eventInfoEventPage);
        event_info.setText (eventInfo);
        String eventPlace = intent.getStringExtra ("eventPlace");
        TextView event_place = (TextView) findViewById (R.id.eventPage_location);
        event_place.setText (eventPlace);
    }

    public void openTicketsPage(View view) {
        lookForUser ();
    }

    public void lookForUser() {
        if (readFromFile ().equals ("")) {
            Intent intent = new Intent (EventPage.this, LoginActivity.class);
            startActivity (intent);
        } else {
            Toast.makeText (getApplicationContext (), "already signed!", Toast.LENGTH_SHORT).show ();
//            Bundle b = new Bundle ();
//            Intent ticketsPageIntent = new Intent (this, TicketsPage.class);
//            Intent intentHere = getIntent ();
//            ticketsPageIntent.putExtra ("eventName", intentHere.getStringExtra ("eventName"));
//            ticketsPageIntent.putExtras (b);
//            startActivity (ticketsPageIntent);
        }
    }

    private String readFromFile() {
        String phone_number = "";
        File f = new File ("verify.txt");
        if (f.exists () && !f.isDirectory ()) {
            try {
                InputStream inputStream = openFileInput ("verify.txt");
                if (inputStream != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
                    BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
                    String receiveString = "";
                    while ((receiveString = bufferedReader.readLine ()) != null) {
                        phone_number = receiveString;
                        Toast.makeText (getApplicationContext (), phone_number, Toast.LENGTH_SHORT).show ();
                    }
                    inputStream.close ();
                }
            } catch (FileNotFoundException e) {
                Log.e ("login activity", "File not found: " + e.toString ());
            } catch (IOException e) {
                Log.e ("login activity", "Can not read file: " + e.toString ());
            }
        }
        return phone_number;
    }
}
