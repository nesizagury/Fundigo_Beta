package com.example.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class EventPage extends Activity {

    private TextView myText = null;
    private String name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_page);
        Intent intent = getIntent();
        int image_id = intent.getIntExtra("eventImage", R.mipmap.pic0);
        ImageView event_image = (ImageView) findViewById(R.id.eventPage_image);
        event_image.setImageResource(image_id);
        String date = intent.getStringExtra ("eventDate");
        TextView event_date = (TextView) findViewById(R.id.eventPage_date);
        event_date.setText (date);
        String eventName = intent.getStringExtra("eventName");
        TextView event_name = (TextView) findViewById(R.id.eventPage_name);
        event_name.setText(eventName);
        String eventTags = intent.getStringExtra("eventTags");
        TextView event_tags = (TextView) findViewById(R.id.eventPage_tags);
        event_tags.setText(eventTags);
        String eventPrice = intent.getStringExtra("eventPrice");
        TextView event_price = (TextView) findViewById(R.id.priceEventPage);
        event_price.setText(eventPrice);
        String eventInfo = intent.getStringExtra("eventInfo");
        TextView event_info = (TextView) findViewById(R.id.eventInfoEventPage);
        event_info.setText(eventInfo);
    }

}
