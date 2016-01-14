package com.example.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class EventPage extends Activity {

    static boolean found = false;
    int producer_id;
    int customer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_event_page);

        found = !readFromFile().equals("");

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
        Bundle b = getIntent().getExtras();
        producer_id = b.getInt("producer_id");
        customer_id = b.getInt("customer_id");
    }

    public void openTicketsPage(View view) {
        if(readFromFile().equals(""))
        {
            Bundle b = new Bundle ();
            Intent intent = new Intent(EventPage.this,LoginActivity.class);
            Intent intentHere = getIntent ();
            intent.putExtra ("eventName", intentHere.getStringExtra ("eventName"));
            intent.putExtras (b);
            startActivity (intent);
        }
        else {
            Toast.makeText (getApplicationContext (), "already signed!", Toast.LENGTH_SHORT).show ();
            Bundle b = new Bundle ();
            Intent ticketsPageIntent = new Intent (EventPage.this, TicketsPage.class);
            Intent intentHere = getIntent ();
            ticketsPageIntent.putExtra ("eventName", intentHere.getStringExtra ("eventName"));
            ticketsPageIntent.putExtras (b);
            startActivity (ticketsPageIntent);
        }
    }

    private String readFromFile() {
        String phone_number = "";
        try {
            InputStream inputStream = openFileInput("verify.txt");
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    phone_number = receiveString;
                    Toast.makeText(getApplicationContext(), phone_number , Toast.LENGTH_SHORT).show();
                }
                inputStream.close();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return phone_number;
    }


    public void openChat(View view){

        if(MainActivity.isCustomer) {
            Intent intent = new Intent(EventPage.this, ChatActivity.class);
            intent.putExtra("producer_id", Integer.toString (producer_id));
            intent.putExtra ("customer_id", Integer.toString (customer_id));
            startActivity(intent);
        }

        else
        if(!MainActivity.isGuest)
            loadMessagesPage();

    }

    private void loadMessagesPage() {

        List<Room> rList = new ArrayList<Room>();
        List<MessageRoomBean> mrbList = new ArrayList<MessageRoomBean> ();
        ParseQuery<Room> query = ParseQuery.getQuery (Room.class);
        query.whereEqualTo("producer_id",Integer.toString(producer_id));
        // Toast.makeText (getApplicationContext (), "producer_id = " + rList.size(), Toast.LENGTH_SHORT).show();

        query.orderByDescending ("createdAt");
        try {
            rList = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < rList.size(); i++)
        {
            mrbList.add(new MessageRoomBean(0,null,"",rList.get(i).getCustomer_id(),Integer.toString(producer_id)));
        }

        /*
        for (int i = 0; i < rList.size(); i++) {

            String combinedId = rList.get(i).getConversationId();
            String customer_number = rList.get(i).getCustomer_id();
            ParseQuery<Message> query2 = ParseQuery.getQuery (Message.class);
            query2.whereEqualTo("combinedID", combinedId);
            query2.orderByAscending("createdAt");
           Message message = new Message();

            try {
                message = query2.getFirst();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            mrbList.add(new MessageRoomBean(0,null,message.getBody(),customer_number));

        }
*/

        Intent intent = new Intent (this, MessagesRoom.class);
        intent.putExtra("array", (Serializable) mrbList);
        intent.putExtra("producer_id",Integer.toString(producer_id));
        startActivity (intent);

    }


}
