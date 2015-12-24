package com.example.events;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView list_view;
    public static List list;
    public static List<EventInfo> events_data = new ArrayList<EventInfo> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        uploadUserData ();
        list_view = (ListView) findViewById (R.id.listView);
        Adapters adapts = new Adapters (this);
        list_view.setAdapter (adapts);
        list_view.setSelector (new ColorDrawable (Color.TRANSPARENT));
        list_view.setOnItemClickListener (this);
    }

    public void uploadUserData() {

        Resources res = this.getResources ();
        String[] eventDate_list;
        String[] eventName_list;
        String[] eventTag_list;
        String[] eventPrice_list;
        String[] eventInfo_list;

        eventName_list = res.getStringArray (R.array.eventNames);
        eventDate_list = res.getStringArray (R.array.eventDates);
        eventTag_list = res.getStringArray (R.array.eventTags);
        eventPrice_list = res.getStringArray (R.array.eventPrice);
        eventInfo_list = res.getStringArray (R.array.eventInfo);

        for (int i = 0; i < 15; i++) {
            events_data.add ( new EventInfo (
                                                    R.mipmap.pic0 + i,
                                                    eventDate_list[i],
                                                    eventName_list[i],
                                                    eventTag_list[i],
                                                    eventPrice_list[i],
                                                    eventInfo_list[i])
            );
        }

    }

    public List addToList() {

        list = new ArrayList ();
        //String[] userName_list;
        //userName_list = getResources().getStringArray(R.array.userNames);

        for (int i = 0; i < 14; i++) {
            list.add (new EventInfo (events_data.get (i).getImageId (),
                                            events_data.get (i).getDate (),
                                            events_data.get (i).getName (),
                                            events_data.get (i).getTags (),
                                            events_data.get (i).getPrice (),
                                            events_data.get (i).getInfo ()));
        }
        for (int i = 0; i < 14; i++) {
            list.add (new EventInfo (events_data.get (i).getImageId (),
                                            events_data.get (i).getDate (),
                                            events_data.get (i).getName (),
                                            events_data.get (i).getTags (),
                                            events_data.get (i).getPrice (),
                                            events_data.get (i).getInfo ()));
        }
        for (int i = 0; i < 14; i++) {
            list.add (new EventInfo (events_data.get (i).getImageId (),
                                            events_data.get (i).getDate (),
                                            events_data.get (i).getName (),
                                            events_data.get (i).getTags (),
                                            events_data.get (i).getPrice (),
                                            events_data.get (i).getInfo ()));
        }
        for (int i = 0; i < 8; i++) {
            list.add (new EventInfo (events_data.get (i).getImageId (),
                                            events_data.get (i).getDate (),
                                            events_data.get (i).getName (),
                                            events_data.get (i).getTags (),
                                            events_data.get (i).getPrice (),
                                            events_data.get (i).getInfo ()));
        }
        return list;

    }

    /**
     * Called when the user clicks the filter button
     */
    public void openFilterPage(View view) {
        Intent filterPageIntent = new Intent (this, FilterPage.class);
        startActivity (filterPageIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPage.class);
        Holder holder = (Holder) view.getTag ();
        EventInfo event = (EventInfo) holder.image.getTag ();
        intent.putExtra ("eventImage", events_data.get (i).getImageId ());
        intent.putExtra ("eventDate", events_data.get (i).getDate ());
        intent.putExtra ("eventName", events_data.get (i).getName ());
        intent.putExtra ("eventTags", events_data.get (i).getTags ());
        intent.putExtra ("eventPrice", events_data.get (i).getPrice ());
        intent.putExtra ("eventInfo", events_data.get (i).getInfo ());
        b.putInt ("userIndex", i);
        intent.putExtras (b);
        startActivity (intent);
    }
}
