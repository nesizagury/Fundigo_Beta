package com.example.FundigoApp.Customer.CustomerMenu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPage;
import com.example.FundigoApp.Events.EventsListAdapter;
import com.example.FundigoApp.MainActivity;
import com.example.FundigoApp.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class EventsTickets extends AppCompatActivity {
    private List<EventInfo> my_tickets_events = new ArrayList<EventInfo> ();
    boolean TICKETS = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_events_tickets);
        ListView listT = (ListView) findViewById (R.id.listOfEventsTickets);

        getListOfEventsTickets ();
        if (TICKETS) {
            ListAdapter _adapter = new EventsListAdapter (EventsTickets.this, my_tickets_events, false);
            listT.setAdapter (_adapter);
        } else
            Toast.makeText (getApplicationContext (), "No Tickets to Display", Toast.LENGTH_SHORT).show ();

        final Intent intent = new Intent (this, EventPage.class); // When click on one of the events that in the list, it will be presented
        listT.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                Bundle b = new Bundle ();
                if (my_tickets_events.get (i).getImageId () != null) {
                    Bitmap bmp = my_tickets_events.get (i).getImageId ();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream ();
                    bmp.compress (Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray ();
                    intent.putExtra ("eventImage", byteArray);
                } else {
                    intent.putExtra ("eventImage", "");
                }
                intent.putExtra ("eventDate", my_tickets_events.get (i).getDate ());
                intent.putExtra ("eventName", my_tickets_events.get (i).getName ());
                intent.putExtra ("eventTags", my_tickets_events.get (i).getTags ());
                intent.putExtra ("eventPrice", my_tickets_events.get (i).getPrice ());
                intent.putExtra ("eventInfo", my_tickets_events.get (i).getInfo ());
                intent.putExtra ("eventPlace", my_tickets_events.get (i).getPlace ());
                intent.putExtra ("toilet", my_tickets_events.get (i).getToilet ());
                intent.putExtra ("parking", my_tickets_events.get (i).getParking ());
                intent.putExtra ("capacity", my_tickets_events.get (i).getCapacity ());
                intent.putExtra ("atm", my_tickets_events.get (i).getAtm ());
                intent.putExtra ("index", my_tickets_events.get (i).getIndexInFullList ());

                b.putString ("customer_id", MainActivity.customer_id);
                if (MainActivity.producerId != null)
                    b.putString ("producer_id", MainActivity.producerId);
                else
                    b.putString ("producer_id", my_tickets_events.get (i).getProducerId ());
                intent.putExtras (b);
                startActivity (intent);
            }
        });
    }

    //this method build a Arraylist of HashMaps from "Tickets" list in parse
    public void getListOfEventsTickets()
    {
        my_tickets_events.clear ();
        String _userPhoneNumber = readFromFile ();
        List<ParseObject> list;
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery ("EventsSeats");
            query.orderByDescending ("purchase_date").whereEqualTo ("buyer_phone", _userPhoneNumber);
            list = query.find ();
            if (list.size () != 0) {
                for (ParseObject obj : list) {
                    my_tickets_events.add (getItemFromAllEventDataList (obj.getString ("eventObjectId")));
                }
                TICKETS = true;
            } else
                TICKETS = false;
        } catch (ParseException e) {
            Log.e ("Exception catch", e.toString ());
        } catch (Exception e) {
            Log.e ("Exception catch", e.toString ());
        }
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

    private EventInfo getItemFromAllEventDataList(String objectId){
        for(EventInfo eventInfo : MainActivity.all_events_data){
            if(eventInfo.getParseObjectId ().equals (objectId)){
                return  eventInfo;
            }
        }
        return null;
    }
}

