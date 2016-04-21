package com.example.FundigoApp.Customer.SavedEvents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.FundigoApp.Customer.CustomerMenu.MenuActivity;
import com.example.FundigoApp.Customer.RealTime.RealTimeActivity;
import com.example.FundigoApp.Customer.Social.MyNotificationsActivity;
import com.example.FundigoApp.Events.CreateEventActivity;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPageActivity;
import com.example.FundigoApp.Events.EventsListAdapter;
import com.example.FundigoApp.Filter.FilterPageActivity;
import com.example.FundigoApp.GlobalVariables;
import com.example.FundigoApp.MyLocation.CityMenu;
import com.example.FundigoApp.R;
import com.example.FundigoApp.SearchActivity;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.example.FundigoApp.StaticMethod.FilterMethods;
import com.example.FundigoApp.StaticMethod.GPSMethods;
import com.example.FundigoApp.StaticMethod.GPSMethods.GpsICallback;
import com.example.FundigoApp.StaticMethod.GeneralStaticMethods;
import com.example.FundigoApp.StaticMethod.EventDataMethods.GetEventsDataCallback;

import java.util.ArrayList;
import java.util.List;

public class SavedEventActivity extends AppCompatActivity implements View.OnClickListener,
                                                                             AdapterView.OnItemClickListener,
                                                                             GetEventsDataCallback,
                                                                             GpsICallback {
    static ArrayList<EventInfo> filteredSavedEventsList = new ArrayList<> ();

    ListView list_view;
    static EventsListAdapter eventsListAdapter;
    Button eventTab;
    Button savedEvent;
    Button realTimeTab;
    static Button currentCityButton;

    static PopupMenu popup;
    static Context context;
    ImageView search, notification;
    private static TextView pushViewText; //assaf: Text view for present the Push messages
    private static SharedPreferences _sharedPref;
    private static TextView filterTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_saved_event);
        context = SavedEventActivity.this;
        list_view = (ListView) findViewById (R.id.listView);
        eventTab = (Button) findViewById (R.id.BarEvent_button);
        savedEvent = (Button) findViewById (R.id.BarSavedEvent_button);
        realTimeTab = (Button) findViewById (R.id.BarRealTime_button);
        notification = (ImageView) findViewById (R.id.notification_item);
        notification.setOnClickListener (this);
        search = (ImageView) findViewById (R.id.search);
        search.setOnClickListener (this);
        pushViewText = (TextView) findViewById (R.id.pushView);
        popup = new PopupMenu (SavedEventActivity.this, currentCityButton);
        currentCityButton = (Button) findViewById (R.id.city_item);
        filterTextView = (TextView) findViewById (R.id.filterView);

        eventsListAdapter = new EventsListAdapter (this, filteredSavedEventsList, true);
        realTimeTab.setOnClickListener (this);
        eventTab.setOnClickListener (this);
        savedEvent.setOnClickListener (this);

        list_view.setAdapter (eventsListAdapter);
        list_view.setSelector (new ColorDrawable (Color.TRANSPARENT));
        list_view.setOnItemClickListener (this);

        if (GlobalVariables.ALL_EVENTS_DATA.size () == 0) {
            Intent intent = new Intent (this, EventPageActivity.class);
            EventDataMethods.downloadEventsData (this, null, this.getApplicationContext (), intent);
        } else {
            inflateCityMenu ();
            getSavedEventsFromJavaList ();
            if (GlobalVariables.MY_LOCATION == null) {
                GPSMethods.updateDeviceLocationGPS (this.context, this);
            }
        }
        GlobalVariables.SAVED_ACTIVITY_RUNNING = true;
    }

    @Override
    protected void onResume() {
        super.onResume ();
        if (GlobalVariables.ALL_EVENTS_DATA.size () != 0) {
            if (GlobalVariables.USER_CHOSEN_CITY_MANUALLY) {
                ArrayList<EventInfo> tempEventsListFiltered =
                        FilterMethods.filterByCityAndFilterName (
                                                                        GlobalVariables.namesCity[GlobalVariables.indexCityChosen],
                                                                        GlobalVariables.CURRENT_FILTER_NAME,
                                                                        GlobalVariables.CURRENT_SUB_FILTER,
                                                                        GlobalVariables.CURRENT_DATE_FILTER,
                                                                        GlobalVariables.CURRENT_PRICE_FILTER,
                                                                        GlobalVariables.ALL_EVENTS_DATA);
                filteredSavedEventsList.clear ();
                filteredSavedEventsList.addAll (getSavedEventsFromList (tempEventsListFiltered));
                eventsListAdapter.notifyDataSetChanged ();
                if (GlobalVariables.CITY_GPS != null &&
                            !GlobalVariables.CITY_GPS.isEmpty () &&
                            GlobalVariables.namesCity[GlobalVariables.indexCityChosen].equals (GlobalVariables.CITY_GPS) &&
                            GPSMethods.getCityIndexFromName (GlobalVariables.CITY_GPS) >= 0) {
                    currentCityButton.setText (GlobalVariables.namesCity[GlobalVariables.indexCityChosen] + "(GPS)");
                } else {
                    currentCityButton.setText (GlobalVariables.namesCity[GlobalVariables.indexCityChosen]);
                }
            } else if (GlobalVariables.CITY_GPS != null &&
                               !GlobalVariables.CITY_GPS.isEmpty () &&
                               GPSMethods.getCityIndexFromName (GlobalVariables.CITY_GPS) >= 0) {
                ArrayList<EventInfo> tempEventsListFiltered =
                        FilterMethods.filterByCityAndFilterName (
                                                                        GlobalVariables.CITY_GPS,
                                                                        GlobalVariables.CURRENT_FILTER_NAME,
                                                                        GlobalVariables.CURRENT_SUB_FILTER,
                                                                        GlobalVariables.CURRENT_DATE_FILTER,
                                                                        GlobalVariables.CURRENT_PRICE_FILTER,
                                                                        GlobalVariables.ALL_EVENTS_DATA);
                filteredSavedEventsList.clear ();
                filteredSavedEventsList.addAll (getSavedEventsFromList (tempEventsListFiltered));
                eventsListAdapter.notifyDataSetChanged ();
                currentCityButton.setText (GlobalVariables.CITY_GPS + "(GPS)");
            } else {
                List<EventInfo> tempEventsList = new ArrayList<> ();
                for (int i = 0; i < GlobalVariables.ALL_EVENTS_DATA.size (); i++) {
                    if (GlobalVariables.ALL_EVENTS_DATA.get (i).getIsSaved ()) {
                        tempEventsList.add (GlobalVariables.ALL_EVENTS_DATA.get (i));
                    }
                }
                filteredSavedEventsList.clear ();
                if (GlobalVariables.CURRENT_FILTER_NAME != null) {
                    tempEventsList = FilterMethods.filterByFilterName (GlobalVariables.CURRENT_FILTER_NAME,
                                                                              GlobalVariables.CURRENT_SUB_FILTER,
                                                                              GlobalVariables.CURRENT_DATE_FILTER,
                                                                              GlobalVariables.CURRENT_PRICE_FILTER,
                                                                              tempEventsList);
                }
                filteredSavedEventsList.addAll (getSavedEventsFromList (tempEventsList));
                eventsListAdapter.notifyDataSetChanged ();
            }
        }
        String[] results = getData (); // display the filter line
        String[] values = getResources ().getStringArray (R.array.eventPriceFilter);// get values from resource
        try {
            if (!results[0].equals ("") || !results[1].equals ("") || !results[2].equals ("") || !results[3].equals ("")) {
                for (int i = 0; i < results.length; i++) {
                    if (results[i].equals (values[0])) //if the result is "No Filter" , we remove it from presemtig it in the filter view
                    {
                        results[i] = "";
                    }
                }
                filterTextView.setVisibility (View.VISIBLE);
                filterTextView.setText (results[0] + " " + results[1] + " " + results[2] + " " + results[3]);
            }
        } catch (Exception ex) {
            Log.e ("TAG", ex.getMessage ());
        }
    }

    @Override
    public void gpsCallback() {
        if (GlobalVariables.CITY_GPS != null && !GlobalVariables.CITY_GPS.isEmpty ()) {
            GlobalVariables.cityMenuInstance = new CityMenu (GlobalVariables.ALL_EVENTS_DATA, this);
            GlobalVariables.namesCity = GlobalVariables.cityMenuInstance.getCityNames ();
            inflateCityMenu ();
            int indexCityGps = GPSMethods.getCityIndexFromName (GlobalVariables.CITY_GPS);
            if (indexCityGps >= 0) {
                popup.getMenu ().getItem (GlobalVariables.indexCityGPS).setTitle (GlobalVariables.namesCity[GlobalVariables.indexCityGPS]);
                GlobalVariables.indexCityGPS = indexCityGps;
                popup.getMenu ().getItem (GlobalVariables.indexCityGPS).setTitle (GlobalVariables.CITY_GPS + "(GPS)");
                if (!GlobalVariables.USER_CHOSEN_CITY_MANUALLY) {
                    ArrayList<EventInfo> tempEventsListFiltered =
                            FilterMethods.filterByCityAndFilterName (
                                                                            GlobalVariables.CITY_GPS,
                                                                            GlobalVariables.CURRENT_FILTER_NAME,
                                                                            GlobalVariables.CURRENT_SUB_FILTER,
                                                                            GlobalVariables.CURRENT_DATE_FILTER,
                                                                            GlobalVariables.CURRENT_PRICE_FILTER,
                                                                            GlobalVariables.ALL_EVENTS_DATA);
                    filteredSavedEventsList.clear ();
                    filteredSavedEventsList.addAll (getSavedEventsFromList (tempEventsListFiltered));
                    eventsListAdapter.notifyDataSetChanged ();
                    currentCityButton.setText (GlobalVariables.CITY_GPS + "(GPS)");
                }
            }
        }
    }

    @Override
    public void eventDataCallback() {
        inflateCityMenu ();
        getSavedEventsFromJavaList ();
        if (GlobalVariables.MY_LOCATION == null) {
            GPSMethods.updateDeviceLocationGPS (this.getApplicationContext (), this);
        }
    }

    public void getSavedEventsFromJavaList() {
        List<EventInfo> tempEventsList = new ArrayList<> ();
        for (int i = 0; i < GlobalVariables.ALL_EVENTS_DATA.size (); i++) {
            if (GlobalVariables.ALL_EVENTS_DATA.get (i).getIsSaved ()) {
                tempEventsList.add (GlobalVariables.ALL_EVENTS_DATA.get (i));
            }
        }
        if (GlobalVariables.CURRENT_FILTER_NAME != null) {
            tempEventsList = FilterMethods.filterByFilterName (GlobalVariables.CURRENT_FILTER_NAME,
                                                                      GlobalVariables.CURRENT_SUB_FILTER,
                                                                      GlobalVariables.CURRENT_DATE_FILTER,
                                                                      GlobalVariables.CURRENT_PRICE_FILTER,
                                                                      tempEventsList);
        }
        filteredSavedEventsList.clear ();
        filteredSavedEventsList.addAll (getSavedEventsFromList (tempEventsList));
        eventsListAdapter.notifyDataSetChanged ();
        if (GlobalVariables.USER_CHOSEN_CITY_MANUALLY) {
            ArrayList<EventInfo> tempEventsListFiltered =
                    FilterMethods.filterByCityAndFilterName (
                                                                    GlobalVariables.namesCity[GlobalVariables.indexCityChosen],
                                                                    GlobalVariables.CURRENT_FILTER_NAME,
                                                                    GlobalVariables.CURRENT_SUB_FILTER,
                                                                    GlobalVariables.CURRENT_DATE_FILTER,
                                                                    GlobalVariables.CURRENT_PRICE_FILTER,
                                                                    GlobalVariables.ALL_EVENTS_DATA);
            filteredSavedEventsList.clear ();
            filteredSavedEventsList.addAll (getSavedEventsFromList (tempEventsListFiltered));
            eventsListAdapter.notifyDataSetChanged ();
            if (GlobalVariables.CITY_GPS != null &&
                        GlobalVariables.namesCity[GlobalVariables.indexCityChosen].equals (GlobalVariables.CITY_GPS) &&
                        GPSMethods.getCityIndexFromName (GlobalVariables.CITY_GPS) >= 0) {
                currentCityButton.setText (GlobalVariables.namesCity[GlobalVariables.indexCityChosen] + "(GPS)");
            } else {
                currentCityButton.setText (GlobalVariables.namesCity[GlobalVariables.indexCityChosen]);
            }
        } else if (GlobalVariables.CITY_GPS != null &&
                           !GlobalVariables.CITY_GPS.isEmpty () &&
                           GPSMethods.getCityIndexFromName (GlobalVariables.CITY_GPS) >= 0) {
            ArrayList<EventInfo> tempEventsListFiltered =
                    FilterMethods.filterByCityAndFilterName (
                                                                    GlobalVariables.CITY_GPS,
                                                                    GlobalVariables.CURRENT_FILTER_NAME,
                                                                    GlobalVariables.CURRENT_SUB_FILTER,
                                                                    GlobalVariables.CURRENT_DATE_FILTER,
                                                                    GlobalVariables.CURRENT_PRICE_FILTER,
                                                                    GlobalVariables.ALL_EVENTS_DATA);
            filteredSavedEventsList.clear ();
            filteredSavedEventsList.addAll (getSavedEventsFromList (tempEventsListFiltered));
            eventsListAdapter.notifyDataSetChanged ();
            currentCityButton.setText (GlobalVariables.CITY_GPS + "(GPS)");
        }
    }

    private void inflateCityMenu() {
        popup = new PopupMenu (SavedEventActivity.this, currentCityButton);//Assaf
        popup.getMenuInflater ().inflate (R.menu.popup_city, popup.getMenu ());

        if (GlobalVariables.namesCity.length == 0) {
            loadCityNamesToPopUp ();
        } else {
            loadCityNamesToPopUp ();
        }
        currentCityButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () {
                    public boolean onMenuItemClick(MenuItem item) {
                        GlobalVariables.indexCityChosen = GlobalVariables.popUpIDToCityIndex.get (item.getItemId ());
                        GlobalVariables.CURRENT_CITY_NAME = item.getTitle ().toString ();
                        if (GlobalVariables.CITY_GPS != null &&
                                    !GlobalVariables.CITY_GPS.isEmpty () &&
                                    item.getTitle ().equals (GlobalVariables.CITY_GPS) &&
                                    GPSMethods.getCityIndexFromName (GlobalVariables.CITY_GPS) >= 0) {
                            currentCityButton.setText (item.getTitle () + "(GPS)");
                        } else {
                            currentCityButton.setText (item.getTitle ());
                        }
                        ArrayList<EventInfo> tempEventsListFiltered =
                                FilterMethods.filterByCityAndFilterName (
                                                                                GlobalVariables.namesCity[GlobalVariables.indexCityChosen],
                                                                                GlobalVariables.CURRENT_FILTER_NAME,
                                                                                GlobalVariables.CURRENT_SUB_FILTER,
                                                                                GlobalVariables.CURRENT_DATE_FILTER,
                                                                                GlobalVariables.CURRENT_PRICE_FILTER,
                                                                                GlobalVariables.ALL_EVENTS_DATA);
                        filteredSavedEventsList.clear ();
                        filteredSavedEventsList.addAll (getSavedEventsFromList (tempEventsListFiltered));
                        eventsListAdapter.notifyDataSetChanged ();
                        GlobalVariables.USER_CHOSEN_CITY_MANUALLY = true;
                        return true;
                    }
                });
                popup.show ();//showing popup menu
            }
        });
    }

    private void loadCityNamesToPopUp() {
        try {
            boolean foundCity = true;
            if (!GlobalVariables.CURRENT_CITY_NAME.isEmpty ()) {
                foundCity = false;
            }
            for (int i = 0; i < GlobalVariables.namesCity.length; i++) {
                if (i == GlobalVariables.indexCityGPS &&
                            GlobalVariables.CITY_GPS != null &&
                            !GlobalVariables.CITY_GPS.isEmpty () &&
                            GPSMethods.getCityIndexFromName (GlobalVariables.CITY_GPS) >= 0) {
                    popup.getMenu ().getItem (i).setTitle (GlobalVariables.namesCity[i] + "(GPS)");
                } else {
                    popup.getMenu ().getItem (i).setTitle (GlobalVariables.namesCity[i]);
                }
                GlobalVariables.popUpIDToCityIndex.put (popup.getMenu ().getItem (i).getItemId (), i);
                if (!GlobalVariables.CURRENT_CITY_NAME.isEmpty () &&
                            GlobalVariables.CURRENT_CITY_NAME.equals (GlobalVariables.namesCity[i])) {
                    GlobalVariables.indexCityChosen = i;
                    foundCity = true;
                }
            }
            if (!foundCity) {
                GlobalVariables.CURRENT_CITY_NAME = "";
                GlobalVariables.indexCityChosen = 0;
            }
            if (GlobalVariables.USER_CHOSEN_CITY_MANUALLY) {
                currentCityButton.setText (popup.getMenu ().getItem (GlobalVariables.indexCityChosen).getTitle ());
            } else if (GlobalVariables.CITY_GPS != null &&
                               !GlobalVariables.CITY_GPS.isEmpty () &&
                               GPSMethods.getCityIndexFromName (GlobalVariables.CITY_GPS) >= 0) {
                currentCityButton.setText (GlobalVariables.CITY_GPS + "(GPS)");
            } else {
                currentCityButton.setText (popup.getMenu ().getItem (0).getTitle ());
            }
            if (GlobalVariables.namesCity.length < 10)
            //in case number of cities is smaller then 10. remove Menu items
            {
                onPrepareOptionsMenu (popup.getMenu ());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public boolean onPrepareOptionsMenu(Menu menu) { //Assaf- Hide the Items in Menu XML which are empty since the length of menu is less then 11
        try {
            super.onPrepareOptionsMenu (menu);
            int maxLength = 11;
            int numOfItemsToRemove = maxLength - GlobalVariables.namesCity.length;
            while (numOfItemsToRemove > 0) {
                menu.getItem (maxLength - 1).setVisible (false);
                numOfItemsToRemove--;
                maxLength--;
            }
        } catch (Exception e) {
            Log.e (e.toString (), "On Prepare Method Exception");
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        Intent newIntent = null;
        if (v.getId () == eventTab.getId ()) {
            finish ();
        } else if (v.getId () == realTimeTab.getId ()) {
            newIntent = new Intent (this, RealTimeActivity.class);
            startActivity (newIntent);
            finish ();
        } else if (v.getId () == search.getId ()) {
            newIntent = new Intent (this, SearchActivity.class);
            startActivity (newIntent);
        } else if (v.getId () == notification.getId ()) {
            newIntent = new Intent (this, MyNotificationsActivity.class);
            startActivity (newIntent);
        }
    }

    public void openFilterPage(View v) {
        Intent filterPageIntent = new Intent (this, FilterPageActivity.class);
        startActivity (filterPageIntent);
    }

    public void openMenuPage(View v) {
        Intent menuPageIntent = new Intent (this, MenuActivity.class);
        startActivity (menuPageIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPageActivity.class);
        EventDataMethods.onEventItemClick (i, filteredSavedEventsList, intent);
        intent.putExtras (b);
        startActivity (intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        GeneralStaticMethods.onActivityResult (requestCode,
                                                      data,
                                                      this);
    }

    public void createEvent(View view) {
        Intent intent = new Intent (SavedEventActivity.this, CreateEventActivity.class);
        startActivity (intent);
    }

    List<EventInfo> getSavedEventsFromList(List<EventInfo> eventInfoList) {
        ArrayList<EventInfo> tempEventsList = new ArrayList<> ();
        for (int i = 0; i < eventInfoList.size (); i++) {
            if (eventInfoList.get (i).getIsSaved ()) {
                tempEventsList.add (eventInfoList.get (i));
            }
        }
        return tempEventsList;
    }

    public void setTextToView(String str) {
        if (pushViewText.equals (null))
            pushViewText = (TextView) findViewById (R.id.pushView);

        pushViewText.setText (str);//Assaf added: set Push notification text to the Textview by MainActivity
    }

    public String[] getData()
    // display the filter info selected by the user.
    {
        _sharedPref = getSharedPreferences ("filterInfo", MODE_PRIVATE);
        String _date = _sharedPref.getString ("date", "");
        String _price = _sharedPref.getString ("price", "");
        String _mainfilter = _sharedPref.getString ("mainFilter", "");
        String _subfilter = _sharedPref.getString ("subFilter", "");

        String[] values = {_mainfilter, _subfilter, _date, _price};

        return values;
    }
}
