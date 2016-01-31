package com.example.events;


import android.Manifest.permission;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.events.MyLocation.LocationResult;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private final static String TAG = "MainActivity";
    static final int REQUEST_CODE_MY_PICK = 1;

    ListView list_view;
    public static List<EventInfo> events_data = new ArrayList<EventInfo> ();
    public static List<EventInfo> filtered_events_data = new ArrayList<EventInfo> ();
    EventsListAdapter eventsListAdapter;
    Button event, savedEvent, realTime, currentCityChosen;

    boolean didInit = false;
    static boolean isCustomer = false;
    static boolean isGuest = false;
    String customer_id;
    static Location loc;
    public static boolean turnGps = true;
    public static boolean gps_enabled = false;
    public static boolean network_enabled = false;
    public static LocationManager LocationServices;
    private static LocationManager locationManager;
    PopupMenu popup;
    String[] namesCity;
    static int indexCityGPS = 0;
    HashMap<Integer, Integer> popUpIDToCityIndex = new HashMap<Integer, Integer> ();
    LocationListener locationListener;
    static boolean userChoosedCityManually = false;
    static boolean cityFoundGPS = false;
    Button create_button;
    boolean isProducer = false;
    static String producerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);//
        list_view = (ListView) findViewById (R.id.listView);
        event = (Button) findViewById (R.id.BarEvent_button);
        savedEvent = (Button) findViewById (R.id.BarSavedEvent_button);
        realTime = (Button) findViewById (R.id.BarRealTime_button);

        popup = new PopupMenu (MainActivity.this, currentCityChosen);
        currentCityChosen = (Button) findViewById (R.id.city_item);
        inflateCityMenu ();
        eventsListAdapter = new EventsListAdapter (this, filtered_events_data);
        realTime.setOnClickListener (this);
        event.setOnClickListener (this);
        savedEvent.setOnClickListener (this);

        if (!didInit) {
            uploadUserData ();
            didInit = true;
        }

        list_view.setAdapter (eventsListAdapter);
        list_view.setSelector (new ColorDrawable (Color.TRANSPARENT));
        list_view.setOnItemClickListener (this);

        Intent intent = getIntent ();
        if (intent.getStringExtra ("chat_id") != null) {
            customer_id = intent.getStringExtra ("chat_id");
            isCustomer = true;
        }
        if (intent.getStringExtra ("is_guest") != null) {
            isGuest = true;
        }
        LocationResult locationResult = new LocationResult () {
            @Override
            public void gotLocation(Location location) {
                if (location != null) {
                    final String cityGPS = findCurrentCityGPS (location);
                    if (!cityGPS.isEmpty ()) {
                        cityFoundGPS = true;
                        popup.getMenu ().getItem (indexCityGPS).setTitle (namesCity[indexCityGPS]);
                        indexCityGPS = getCityIndexFromName (cityGPS);
                        popup.getMenu ().getItem (indexCityGPS).setTitle (cityGPS + "(GPS)");
                        if (!userChoosedCityManually) {
                            filterByCity (cityGPS);
                            runOnUiThread (new Runnable () {
                                @Override
                                public void run() {
                                    currentCityChosen.setText (cityGPS + "(GPS)");
                                    eventsListAdapter.notifyDataSetChanged ();
                                }
                            });

                        }

                    }
                }
            }
        };
        MyLocation myLocation = new MyLocation (this.getApplicationContext ());
        myLocation.getLocation (this, locationResult);
        updateDeviceLocationGPS ();
        if (intent.getStringExtra ("is_producer") != null) {
            isProducer = true;
            producerId = intent.getStringExtra ("producerId");
            create_button = (Button) findViewById (R.id.create_button);
            create_button.setVisibility (View.VISIBLE);
        }
        SharedPreferences ratePrefs = getSharedPreferences ("First Update", 0);
        if (!ratePrefs.getBoolean ("FrstTime", false)) {
            ParsePush.subscribeInBackground ("All_Users");
            SharedPreferences.Editor edit = ratePrefs.edit ();
            edit.putBoolean ("FrstTime", true);
            edit.commit ();
        }
    }

    private void uploadUserData() {
        events_data.clear ();
        filtered_events_data.clear ();
        ParseQuery<Event> query = new ParseQuery ("Event");
        query.orderByDescending ("createdAt");
        List<Event> eventParses = null;

        try {
            eventParses = query.find ();
            ParseFile imageFile;
            byte[] data;
            Bitmap bmp;
            for (int i = 0; i < eventParses.size (); i++) {
                imageFile = (ParseFile) eventParses.get (i).get ("ImageFile");
                if (imageFile != null) {
                    data = imageFile.getData ();
                    bmp = BitmapFactory.decodeByteArray (data, 0, data.length);
                } else
                    bmp = null;

                events_data.add (new EventInfo (
                                                       bmp,
                                                       eventParses.get (i).getDate (),
                                                       eventParses.get (i).getName (),
                                                       eventParses.get (i).getTags (),
                                                       eventParses.get (i).getPrice (),
                                                       eventParses.get (i).getDescription (),
                                                       eventParses.get (i).getAddress (),
                                                       eventParses.get (i).getEventToiletService (),
                                                       eventParses.get (i).getEventParkingService (),
                                                       eventParses.get (i).getEventCapacityService (),
                                                       eventParses.get (i).getEventATMService (),
                                                       eventParses.get (i).getCity ()));
                events_data.get (i).setProducerId (eventParses.get (i).getProducerId ());
            }
            filtered_events_data.addAll (events_data);
        } catch (ParseException e) {
            e.printStackTrace ();
        }
    }

    private void inflateCityMenu() {
        popup.getMenuInflater ().inflate (R.menu.popup_city, popup.getMenu ());
        loadCityNamesToPopUp ();
        if (userChoosedCityManually) {
            filterByCity (namesCity[0]);
        } else {
            filterByCity (namesCity[indexCityGPS]);
        }
        currentCityChosen.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                popup = new PopupMenu (MainActivity.this, currentCityChosen);
                //Inflating the Popup using xml file
                popup.getMenuInflater ().inflate (R.menu.popup_city, popup.getMenu ());
                for (int i = 0; i < namesCity.length; i++) {
                    if (i == indexCityGPS && cityFoundGPS) {
                        popup.getMenu ().getItem (i).setTitle (namesCity[i] + "(GPS)");
                    } else {
                        popup.getMenu ().getItem (i).setTitle (namesCity[i]);
                    }
                }
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () {
                    public boolean onMenuItemClick(MenuItem item) {
                        int i = popUpIDToCityIndex.get (item.getItemId ());
                        currentCityChosen.setText (item.getTitle ());
                        filterByCity (namesCity[i]);
                        eventsListAdapter.notifyDataSetChanged ();
                        userChoosedCityManually = true;
                        return true;
                    }
                });
                popup.show ();//showing popup menu
            }
        });
    }

    private void loadCityNamesToPopUp() {
        Resources rsc = getResources ();
        namesCity = rsc.getStringArray (R.array.popUp);
        try {
            for (int i = 0; i < namesCity.length; i++) {
                if (i == indexCityGPS && cityFoundGPS) {
                    popup.getMenu ().getItem (i).setTitle (namesCity[i] + "(GPS)");
                } else {
                    popup.getMenu ().getItem (i).setTitle (namesCity[i]);
                }
                popUpIDToCityIndex.put (popup.getMenu ().getItem (i).getItemId (), i);
            }
            if (userChoosedCityManually) {
                currentCityChosen.setText (popup.getMenu ().getItem (0).getTitle ());
            } else {
                currentCityChosen.setText (popup.getMenu ().getItem (indexCityGPS).getTitle ());
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void filterByCity(String cityName) {
        filtered_events_data.clear ();
        if (cityName.equals ("All Cities")) {
            filtered_events_data.addAll (events_data);
            return;
        } else {
            for (int i = 0; i < events_data.size (); i++) {
                String cityEvent = events_data.get (i).getCity ();
                if (cityEvent != null && cityEvent.equals (cityName)) {
                    filtered_events_data.add (events_data.get (i));
                }
            }
        }
    }

    public void updateDeviceLocationGPS() {
        boolean gps_enabled = false;
        boolean network_enabled = false;
        boolean passive_enabled = false;

        locationManager = (LocationManager) this.getSystemService (Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener ();
        try {
            gps_enabled = locationManager.isProviderEnabled (LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            network_enabled = locationManager.isProviderEnabled (LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            passive_enabled = locationManager.isProviderEnabled (LocationManager.PASSIVE_PROVIDER);
        } catch (Exception ex) {
        }
        if (gps_enabled) {
            if (ActivityCompat.checkSelfPermission (this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //do none
            } else {
                locationManager.requestLocationUpdates (LocationManager.GPS_PROVIDER, 10000, 0, locationListener);

            }
        }
        if (network_enabled) {
            locationManager.requestLocationUpdates (LocationManager.NETWORK_PROVIDER, 10000, 0, locationListener);
        }
        if (passive_enabled) {
            locationManager.requestLocationUpdates (LocationManager.PASSIVE_PROVIDER, 10000, 0, locationListener);
        }
    }

    @Override
    public void onClick(View v) {
        Intent newIntent = null;
        if (v.getId () == savedEvent.getId ()) {
            newIntent = new Intent (this, SavedEvent.class);
            startActivity (newIntent);
        } else if (v.getId () == realTime.getId ()) {
            newIntent = new Intent (this, RealTime.class);
            startActivity (newIntent);
        }
    }

    public void openFilterPage(View v) {
        Intent filterPageIntent = new Intent (this, FilterPage.class);
        startActivity (filterPageIntent);
    }

    public void openMenuPage(View v) {
        Intent menuPageIntent = new Intent (this, com.example.events.Menu.class);
        startActivity (menuPageIntent);
    }

    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        Bundle b = new Bundle ();
        Intent intent = new Intent (this, EventPage.class);
        if (filtered_events_data.get (i).getImageId () != null) {
            Bitmap bmp = filtered_events_data.get (i).getImageId ();
            ByteArrayOutputStream stream = new ByteArrayOutputStream ();
            bmp.compress (Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray ();
            intent.putExtra ("eventImage", byteArray);
        } else
            intent.putExtra ("eventImage", "");
        intent.putExtra ("eventDate", filtered_events_data.get (i).getDate ());
        intent.putExtra ("eventName", filtered_events_data.get (i).getName ());
        intent.putExtra ("eventTags", filtered_events_data.get (i).getTags ());
        intent.putExtra ("eventPrice", filtered_events_data.get (i).getPrice ());
        intent.putExtra ("eventInfo", filtered_events_data.get (i).getInfo ());
        intent.putExtra ("eventPlace", filtered_events_data.get (i).getPlace ());
        intent.putExtra ("toilet", filtered_events_data.get (i).getToilet ());
        intent.putExtra ("parking", filtered_events_data.get (i).getParking ());
        intent.putExtra ("capacity", filtered_events_data.get (i).getCapacity ());
        intent.putExtra ("atm", filtered_events_data.get (i).getAtm ());
        intent.putExtra ("index", i);

        b.putString ("customer_id", customer_id);
        if (producerId != null)
            b.putString ("producer_id", producerId);
        else
            b.putString ("producer_id", filtered_events_data.get (i).getProducerId ());
        intent.putExtras (b);
        startActivity (intent);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (data != null && requestCode == REQUEST_CODE_MY_PICK) {
            String appName = data.getComponent ().flattenToShortString ();
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (this);
            String name = sp.getString ("name", null);
            String date = sp.getString ("date", null);
            String place = sp.getString ("place", null);
            if (appName.equals ("com.facebook.katana/com.facebook.composer.shareintent.ImplicitShareIntentHandlerDefaultAlias")) {
                ShareDialog shareDialog;
                shareDialog = new ShareDialog (this);
                ShareLinkContent linkContent = new ShareLinkContent.Builder ()
                                                       .setContentTitle ("I`m going to " + name)
                                                       .setImageUrl (Uri.parse ("https://lh3.googleusercontent.com/-V5wz7jKaQW8/VpvKq0rwEOI/AAAAAAAAB6Y/cZoicmGpQpc/s279-Ic42/pic0.jpg"))
                                                       .setContentDescription (
                                                                                      "C u there at " + date + " !" + "\n" + "At " + place)
                                                       .setContentUrl (Uri.parse ("http://eventpageURL.com/here"))
                                                       .build ();
                shareDialog.show (linkContent);
            } else {
                startActivity (data);
            }
        }
    }

    private class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                String cityGPS = findCurrentCityGPS (location);
                if (!cityGPS.isEmpty ()) {
                    cityFoundGPS = true;
                    popup.getMenu ().getItem (indexCityGPS).setTitle (namesCity[indexCityGPS]);
                    indexCityGPS = getCityIndexFromName (cityGPS);
                    popup.getMenu ().getItem (indexCityGPS).setTitle (cityGPS + "(GPS)");
                    if (!userChoosedCityManually) {
                        filterByCity (cityGPS);
                        currentCityChosen.setText (cityGPS + "(GPS)");
                        eventsListAdapter.notifyDataSetChanged ();
                    }

                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    public String findCurrentCityGPS(Location loc) {
        Geocoder gcd = new Geocoder (this, Locale.ENGLISH);
        if (loc != null) {
            List<Address> addresses = null;
            try {
                addresses = gcd.getFromLocation (loc.getLatitude (), loc.getLongitude (), 1);
            } catch (IOException e) {
                e.printStackTrace ();
            }
            if (addresses != null && addresses.size () > 0) {
                String city = addresses.get (0).getLocality ();
                for (int i = 0; i < popup.getMenu ().size (); i++) {
                    MenuItem menuItem = popup.getMenu ().getItem (i);
                    if (menuItem.getTitle ().equals (city)) {
                        return city;
                    }
                }
            }
        }
        return "";
    }

    private int getCityIndexFromName(String name) {
        for (int i = 0; i < namesCity.length; i++) {
            String city = namesCity[i];
            if (city.equals (name)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onPause() {
        super.onPause ();
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission (this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission (this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates (locationListener);
        }
    }

    @Override
    public void onResume() {
        super.onResume ();
        updateDeviceLocationGPS ();
        eventsListAdapter.notifyDataSetChanged ();
    }

    public void createEvent(View view) {

        Intent intent = new Intent (MainActivity.this, CreateEventActivity.class);
        startActivity (intent);

    }
}
