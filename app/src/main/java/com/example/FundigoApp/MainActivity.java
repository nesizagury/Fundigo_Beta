package com.example.FundigoApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.example.FundigoApp.Customer.CustomerMenu.MenuActivity;
import com.example.FundigoApp.Customer.RealTime.RealTimeActivity;
import com.example.FundigoApp.Customer.SavedEvents.SavedEventActivity;
import com.example.FundigoApp.Customer.Social.MyNotificationsActivity;
import com.example.FundigoApp.Events.CreateEventActivity;
import com.example.FundigoApp.Events.EventInfo;
import com.example.FundigoApp.Events.EventPageActivity;
import com.example.FundigoApp.Events.EventsListAdapter;
import com.example.FundigoApp.Filter.FilterPageActivity;
import com.example.FundigoApp.MyLocation.CityMenu;
import com.example.FundigoApp.Producer.TabPagerAdapter;
import com.example.FundigoApp.StaticMethod.EventDataMethods;
import com.example.FundigoApp.StaticMethod.EventDataMethods.GetEventsDataCallback;
import com.example.FundigoApp.StaticMethod.FilterMethods;
import com.example.FundigoApp.StaticMethod.GPSMethods;
import com.example.FundigoApp.StaticMethod.GPSMethods.GpsICallback;
import com.example.FundigoApp.StaticMethod.GeneralStaticMethods;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener,
                                                                       View.OnClickListener,
                                                                       GetEventsDataCallback,
                                                                       GpsICallback {

    ListView list_view;
    private static List<EventInfo> filtered_events_data = new ArrayList<EventInfo> ();
    private static EventsListAdapter eventsListAdapter;
    Button event, savedEvent, realTime;
    static Button currentCityButton;
    ImageView search, notification;
    private static TextView pushViewText;
    static PopupMenu popup;
    Context context;
    private static SharedPreferences _sharedPref;
    private static TextView filterTextView;
    PushDisplay display;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (GlobalVariables.IS_CUSTOMER_GUEST || GlobalVariables.IS_CUSTOMER_REGISTERED_USER) {
            createCustomerMainPage ();
        } else if (GlobalVariables.IS_PRODUCER) {
            createProducerMainPage ();
        }
    }

    public void createProducerMainPage() {
        setContentView (R.layout.producer_avtivity_main);

        TabLayout tabLayout = (TabLayout) findViewById (R.id.tab_layout);
        if (Locale.getDefault ().getDisplayLanguage ().equals ("עברית")) {
            tabLayout.addTab (tabLayout.newTab ().setText ("אמנים"));
            tabLayout.addTab (tabLayout.newTab ().setText ("מידע"));
        } else {
            tabLayout.addTab (tabLayout.newTab ().setText ("Artist"));
            tabLayout.addTab (tabLayout.newTab ().setText ("State"));
        }

        tabLayout.setTabGravity (TabLayout.GRAVITY_FILL);
        final ViewPager viewPager = (ViewPager) findViewById (R.id.pager);
        final TabPagerAdapter adapter = new TabPagerAdapter
                                                (getSupportFragmentManager (), tabLayout.getTabCount ());
        viewPager.setAdapter (adapter);
        viewPager.addOnPageChangeListener (new TabLayout.TabLayoutOnPageChangeListener (tabLayout));
        tabLayout.setOnTabSelectedListener (new TabLayout.OnTabSelectedListener () {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem (tab.getPosition ());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    public void createCustomerMainPage() {
        setContentView (R.layout.activity_main);

        list_view = (ListView) findViewById (R.id.listView);
        event = (Button) findViewById (R.id.BarEvent_button);
        savedEvent = (Button) findViewById (R.id.BarSavedEvent_button);
        realTime = (Button) findViewById (R.id.BarRealTime_button);
        notification = (ImageView) findViewById (R.id.notification_item);
        notification.setOnClickListener (this);

        context = this;

        currentCityButton = (Button) findViewById (R.id.city_item);
        eventsListAdapter = new EventsListAdapter (this, filtered_events_data, false);
        realTime.setOnClickListener (this);
        event.setOnClickListener (this);
        savedEvent.setOnClickListener (this);
        filterTextView = (TextView) findViewById (R.id.filterView);

        search = (ImageView) findViewById (R.id.search);
        search.setOnClickListener (this);

        list_view.setAdapter (eventsListAdapter);
        list_view.setSelector (new ColorDrawable (Color.TRANSPARENT));
        list_view.setOnItemClickListener (this);

        if (GlobalVariables.ALL_EVENTS_DATA.size () == 0) {
            Intent intent = new Intent (this, EventPageActivity.class);
            EventDataMethods.downloadEventsData (this, null, this.context, intent);
        } else {
            inflateCityMenu ();
            filtered_events_data.clear ();
            filtered_events_data.addAll (GlobalVariables.ALL_EVENTS_DATA);
            eventsListAdapter.notifyDataSetChanged ();
            FilterMethods.filterListsAndUpdateListAdapter (filtered_events_data,
                                                                  eventsListAdapter,
                                                                  GlobalVariables.namesCity,
                                                                  GlobalVariables.indexCityChosen);
            if (GlobalVariables.MY_LOCATION == null) {
                GPSMethods.updateDeviceLocationGPS (this.context, this);
            }
        }
        pushViewText = (TextView) findViewById (R.id.pushView);
    }

    @Override
    public void eventDataCallback() {
        filtered_events_data.clear ();
        filtered_events_data.addAll (GlobalVariables.ALL_EVENTS_DATA);
        eventsListAdapter.notifyDataSetChanged ();
        inflateCityMenu ();//assaf added to call this message here and not from OnCreate
        FilterMethods.filterListsAndUpdateListAdapter (filtered_events_data,
                                                              eventsListAdapter,
                                                              GlobalVariables.namesCity,
                                                              GlobalVariables.indexCityChosen);
        if (GlobalVariables.MY_LOCATION == null) {
            GPSMethods.updateDeviceLocationGPS (this.context, this);
        }
    }

    @Override
    public void gpsCallback() {
        if (GlobalVariables.CITY_GPS != null &&
                    !GlobalVariables.CITY_GPS.isEmpty ()) {
            GlobalVariables.cityMenuInstance = new CityMenu (GlobalVariables.ALL_EVENTS_DATA, this);
            GlobalVariables.namesCity = GlobalVariables.cityMenuInstance.getCityNames ();
            inflateCityMenu ();
            int indexCityGps = GPSMethods.getCityIndexFromName (GlobalVariables.CITY_GPS);
            if (indexCityGps >= 0) {
                popup.getMenu ().getItem (GlobalVariables.indexCityGPS).setTitle (GlobalVariables.namesCity[GlobalVariables.indexCityGPS]);
                GlobalVariables.indexCityGPS = indexCityGps;
                popup.getMenu ().getItem (GlobalVariables.indexCityGPS).setTitle (GlobalVariables.CITY_GPS + "(GPS)");
                if (!GlobalVariables.USER_CHOSEN_CITY_MANUALLY) {
                    ArrayList<EventInfo> tempEventsList =
                            FilterMethods.filterByCityAndFilterName (
                                                                            GlobalVariables.CITY_GPS,
                                                                            GlobalVariables.CURRENT_FILTER_NAME,
                                                                            GlobalVariables.CURRENT_SUB_FILTER,
                                                                            GlobalVariables.CURRENT_DATE_FILTER,
                                                                            GlobalVariables.CURRENT_PRICE_FILTER,
                                                                            GlobalVariables.ALL_EVENTS_DATA);
                    filtered_events_data.clear ();
                    filtered_events_data.addAll (tempEventsList);
                    eventsListAdapter.notifyDataSetChanged ();
                    currentCityButton.setText (GlobalVariables.CITY_GPS + "(GPS)");
                }
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume ();
        if (!GlobalVariables.IS_PRODUCER) {
            display = new PushDisplay (); // Assaf :execute the the push notifications display in the Textview
            display.execute ();
            if (GlobalVariables.USER_CHOSEN_CITY_MANUALLY) {
                ArrayList<EventInfo> tempEventsList =
                        FilterMethods.filterByCityAndFilterName (
                                                                        GlobalVariables.namesCity[GlobalVariables.indexCityChosen],
                                                                        GlobalVariables.CURRENT_FILTER_NAME,
                                                                        GlobalVariables.CURRENT_SUB_FILTER,
                                                                        GlobalVariables.CURRENT_DATE_FILTER,
                                                                        GlobalVariables.CURRENT_PRICE_FILTER,
                                                                        GlobalVariables.ALL_EVENTS_DATA);
                filtered_events_data.clear ();
                filtered_events_data.addAll (tempEventsList);
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
                ArrayList<EventInfo> tempEventsList =
                        FilterMethods.filterByCityAndFilterName (
                                                                        GlobalVariables.CITY_GPS,
                                                                        GlobalVariables.CURRENT_FILTER_NAME,
                                                                        GlobalVariables.CURRENT_SUB_FILTER,
                                                                        GlobalVariables.CURRENT_DATE_FILTER,
                                                                        GlobalVariables.CURRENT_PRICE_FILTER,
                                                                        GlobalVariables.ALL_EVENTS_DATA);
                filtered_events_data.clear ();
                filtered_events_data.addAll (tempEventsList);
                eventsListAdapter.notifyDataSetChanged ();
                currentCityButton.setText (GlobalVariables.CITY_GPS + "(GPS)");
            } else {
                ArrayList<EventInfo> tempEventsList =
                        FilterMethods.filterByCityAndFilterName (
                                                                        GlobalVariables.namesCity[GlobalVariables.indexCityChosen],
                                                                        GlobalVariables.CURRENT_FILTER_NAME,
                                                                        GlobalVariables.CURRENT_SUB_FILTER,
                                                                        GlobalVariables.CURRENT_DATE_FILTER,
                                                                        GlobalVariables.CURRENT_PRICE_FILTER,
                                                                        GlobalVariables.ALL_EVENTS_DATA);
                filtered_events_data.clear ();
                filtered_events_data.addAll (tempEventsList);
                eventsListAdapter.notifyDataSetChanged ();
            }
            // display the filter line
            try {
                String[] results = getData ();
                String[] values = getResources ().getStringArray (R.array.eventPriceFilter);
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
    }

    private void inflateCityMenu() {
        popup = new PopupMenu (MainActivity.this, currentCityButton);//Assaf added
        popup.getMenuInflater ().inflate (R.menu.popup_city, popup.getMenu ());//Assaf added
        loadCityNamesToPopUp ();
        currentCityButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () {
                    public boolean onMenuItemClick(MenuItem item) {
                        GlobalVariables.indexCityChosen = GlobalVariables.popUpIDToCityIndex.get (item.getItemId ());
                        GlobalVariables.CURRENT_CITY_NAME = item.getTitle ().toString ();
                        if (GlobalVariables.CITY_GPS != null &&
                                    item.getTitle ().equals (GlobalVariables.CITY_GPS) &&
                                    GPSMethods.getCityIndexFromName (GlobalVariables.CITY_GPS) >= 0) {
                            currentCityButton.setText (item.getTitle () + "(GPS)");
                        } else {
                            currentCityButton.setText (item.getTitle ());
                        }
                        ArrayList<EventInfo> tempEventsList =
                                FilterMethods.filterByCityAndFilterName (
                                                                                GlobalVariables.namesCity[GlobalVariables.indexCityChosen],
                                                                                GlobalVariables.CURRENT_FILTER_NAME,
                                                                                GlobalVariables.CURRENT_SUB_FILTER,
                                                                                GlobalVariables.CURRENT_DATE_FILTER,
                                                                                GlobalVariables.CURRENT_PRICE_FILTER,
                                                                                GlobalVariables.ALL_EVENTS_DATA);
                        filtered_events_data.clear ();
                        filtered_events_data.addAll (tempEventsList);
                        eventsListAdapter.notifyDataSetChanged ();
                        GlobalVariables.USER_CHOSEN_CITY_MANUALLY = true;
                        return true;
                    }
                });
                popup.show ();//showing popup menu
            }
        });
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        //Hide the Items in Menu XML which are empty since the length of menu is less then 11
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

    private void loadCityNamesToPopUp() {
        try {
            boolean foundCity = true;
            if (!GlobalVariables.CURRENT_CITY_NAME.isEmpty ()) {
                foundCity = false;
            }
            for (int i = 0; i < GlobalVariables.namesCity.length; i++) {
                if (i == GlobalVariables.indexCityGPS &&
                            GlobalVariables.CITY_GPS != null &&
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
            } else if (GlobalVariables.CITY_GPS != null && GPSMethods.getCityIndexFromName (GlobalVariables.CITY_GPS) >= 0) {
                currentCityButton.setText (GlobalVariables.CITY_GPS + "(GPS)");
            } else {
                currentCityButton.setText (popup.getMenu ().getItem (0).getTitle ());
            }
        } catch (Exception e) {
            throw e;
        }
        if (GlobalVariables.namesCity.length < 10) // Assaf in case number of cities is smaller then 10. remove Menu items
        {
            onPrepareOptionsMenu (popup.getMenu ());
        }
    }

    @Override
    public void onClick(View v) {
        Intent newIntent = null;
        if (v.getId () == savedEvent.getId ()) {
            newIntent = new Intent (this, SavedEventActivity.class);
            startActivity (newIntent);
        } else if (v.getId () == realTime.getId ()) {
            newIntent = new Intent (this, RealTimeActivity.class);
            startActivity (newIntent);
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
        EventDataMethods.onEventItemClick (i, filtered_events_data, intent);
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
        Intent intent = new Intent (MainActivity.this, CreateEventActivity.class);
        intent.putExtra ("create", "true");
        startActivity (intent);
    }

    @Override
    public void onStart() {
        super.onStart ();
        Branch branch = Branch.getInstance (getApplicationContext ());
        branch.initSession (new Branch.BranchReferralInitListener () {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked before showing up
                    try {
                        GlobalVariables.deepLink_params = referringParams.getString ("objectId");
                        for (int i = 0; i < filtered_events_data.size (); i++) {
                            if (GlobalVariables.deepLink_params.equals (filtered_events_data.get (i).getParseObjectId ())) {
                                Intent intent = new Intent (context, EventPageActivity.class);
                                Bundle b = new Bundle ();
                                EventDataMethods.onEventItemClick (i, GlobalVariables.ALL_EVENTS_DATA, intent);
                                intent.putExtras (b);
                                context.startActivity (intent);
                                i = filtered_events_data.size ();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace ();
                    }
                } else
                    Toast.makeText (getApplicationContext (), error.getMessage (), Toast.LENGTH_SHORT).show ();
            }
        }, this.getIntent ().getData (), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent (intent);
    }

    private class PushDisplay extends AsyncTask<Void, String, String> { // Display Push Events in Text View banner
        List<ParseObject> pushObjectsList = new ArrayList<ParseObject> ();

        @Override
        protected String doInBackground(Void... params) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery ("Push");
            query.orderByDescending ("createdAt");
            try {
                pushObjectsList = query.find ();
            } catch (ParseException e) {
                e.printStackTrace ();
            }
            // the push notifications
            try {
                while (true) {
                    for (ParseObject parseObject : pushObjectsList) {
                        if(isCancelled ()){
                            return "";
                        }
                        publishProgress (parseObject.getString ("pushMessage"));
                        try {
                            Thread.sleep (4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace ();
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e (ex.getMessage (), "error");
            }
            return "";
        }

        @Override
        protected void onProgressUpdate(String... text) {
            final Handler hand = getHandler ();

            Message msg = hand.obtainMessage ();
            Bundle bund = new Bundle ();

            bund.putString ("text", text[0]);
            msg.setData (bund);
            hand.sendMessage (msg);
        }
    }

    @Override
    protected void onDestroy() {
        try {
            super.onDestroy ();
            _sharedPref = getSharedPreferences ("filterInfo", MODE_PRIVATE);
            _sharedPref.edit ().clear ().commit ();
            System.exit (0);     // for kill  background Threads that operate the Endless loop of present the push messages
        } catch (Exception ex) {
            Log.e (ex.getMessage (), "onDestroy exception");
        }
    }

    public String[] getData()
    // display the filter info selected by the user.
    {
        _sharedPref = getSharedPreferences ("filterInfo", MODE_PRIVATE);
        String _date = _sharedPref.getString ("date", "");
        String _price = _sharedPref.getString ("price", "");
        String _mainfilter = _sharedPref.getString ("mainFilter", "");
        String _subfilter = _sharedPref.getString ("subFilter", "");

        String[] _results = {_mainfilter, _subfilter, _date, _price};

        return _results;
    }

    private static class mainHandler extends Handler {
        //Using a weak reference means you won't prevent garbage collection
        private final WeakReference<MainActivity> mainWeakReference;
        SavedEventActivity savedEveAct = new SavedEventActivity ();
        RealTimeActivity realTimeAct = new RealTimeActivity ();

        public mainHandler(MainActivity mainInstance) {
            mainWeakReference = new WeakReference<MainActivity> (mainInstance);
        }

        @Override
        public void handleMessage(Message handleMessage) {
            try {
                MainActivity mainA = mainWeakReference.get ();
                if (mainA != null) {
                    pushViewText.setText (handleMessage.getData ().getString ("text"));
                    savedEveAct.setTextToView (handleMessage.getData ().getString ("text"));
                    realTimeAct.setTextToView (handleMessage.getData ().getString ("text"));
                }
            } catch (Exception ex) {
                Log.e (ex.getMessage (), "exception in setText");
            }
        }

    }

    public Handler getHandler() {
        return new mainHandler (this);
    }

    @Override
    public void onPause() {
        super.onPause ();  // Always call the superclass method first
        if(display != null) {
            display.cancel (true);
        }
    }
}
