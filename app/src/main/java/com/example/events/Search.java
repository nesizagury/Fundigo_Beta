package com.example.events;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by benjamin on 27/01/2016.
 */
public class Search extends AppCompatActivity implements SearchView.OnClickListener {
    AutoCompleteTextView autoCompleteTextView;
    SearchView search;
    ListView listView;
    EventsListAdapter listAdpter;
    Button b_history, b_clear;
    String wordSearch;
    ArrayList<String> history = new ArrayList<> ();
    SimpleCursorAdapter cursorAdapter;
    ArrayAdapter<String> autoStrings;
    PopupMenu historyPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_search);
        readHistory ();

        search = (SearchView) findViewById (R.id.b_search);
        b_history = (Button) findViewById (R.id.b_history);
        b_clear = (Button) findViewById (R.id.b_clear);
        listView = (ListView) findViewById (R.id.listView_Search);

        b_clear.setOnClickListener (this);
        search.setOnClickListener (this);

        String[] auto = getResources ().getStringArray (R.array.autoComplateStrings);
        autoStrings = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, auto);
        List<EventInfo> list = new ArrayList<EventInfo> ();
        listAdpter = new EventsListAdapter (this, list, false);
        listView.setAdapter (listAdpter);
        //autoCompleteTextView=(AutoCompleteTextView)findViewById(R.id.autoComplate);
        //autoCompleteTextView.setAdapter(autoStrings);
        //autoCompleteTextView.setThreshold(1);

        //Creating the instance of PopupMenu
        historyPop = new PopupMenu (Search.this, b_history);
        //Inflating the Popup using xml file
        historyPop.getMenuInflater ().inflate (R.menu.popup_city, historyPop.getMenu ());
        b_history = (Button) findViewById (R.id.b_history);

        b_history.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                if (history.size () > 0) {
                    //Creating the instance of PopupMenu
                    historyPop = new PopupMenu (Search.this, b_history);
                    //Inflating the Popup using xml file
                    historyPop.getMenuInflater ().inflate (R.menu.popup_history, historyPop.getMenu ());
                    //registering popup with OnMenuItemClickListener
                    for (int i = 0; i < history.size (); i++) {
                        historyPop.getMenu ().add (history.get (i));
                    }
                    historyPop.setOnMenuItemClickListener (new PopupMenu.OnMenuItemClickListener () {
                        public boolean onMenuItemClick(MenuItem item) {
                            autoCompleteTextView.setText (item.getTitle ());
                            return true;
                        }
                    });

                    historyPop.show ();//showing popup menu
                } else {
                    Toast.makeText (Search.this, "Not have history", Toast.LENGTH_SHORT).show ();
                }
            }
        });/*
        MatrixCursor cursor = new MatrixCursor(auto);
        cursorAdapter =new SimpleCursorAdapter(this,android.R.layout.simple_list_item_1,cursor, new String[] { "autoComplateStrings" },new int[]{ android.R.id.text1});
        search.setSuggestionsAdapter(cursorAdapter);*/
        search.setOnQueryTextListener (new SearchView.OnQueryTextListener () {
            @Override
            public boolean onQueryTextSubmit(String query) {
                wordSearch = search.getQuery ().toString ();
                if (wordSearch.length () == 0) {
                    Toast.makeText (Search.this, "Input word to search", Toast.LENGTH_SHORT).show ();
                } else {
                    wordSearch += "\n";
                    history.add (0, wordSearch);
                    saveHistory ();
                    listAdpter = new EventsListAdapter (Search.this, "filter", searchInfo (wordSearch));
                    listView.setAdapter (listAdpter);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                wordSearch = newText;
                listAdpter = new EventsListAdapter (Search.this, "filter", searchInfo (wordSearch));
                listView.setAdapter (listAdpter);

                return false;
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view.getId () == search.getId ()) {
            wordSearch = search.getQuery ().toString ();
            if (wordSearch.length () > 0) {
                history.add (0, wordSearch);
                saveHistory ();
            }
        }
        if (view.getId () == b_clear.getId ()) {
            clearHistory ();
        }
    }

    private ArrayList<EventInfo> searchInfo(String search) {
        boolean flag = true;
        ArrayList<EventInfo> ans = new ArrayList<> ();
        ArrayList<String> checkIfInside = new ArrayList<> ();
        for (int i = 0; i < MainActivity.all_events_data.size (); i++) {
            if (!checkIfInside.contains (MainActivity.all_events_data.get (i).getName ())) {
                if (MainActivity.all_events_data.get (i).getInfo ().toLowerCase ().contains (search.toLowerCase ())) {
                    ans.add (MainActivity.all_events_data.get (i));
                    checkIfInside.add (MainActivity.all_events_data.get (i).getName ());
                    flag = false;
                }

                if (flag && MainActivity.all_events_data.get (i).getName ().toLowerCase ().contains (search.toLowerCase ())) {
                    ans.add (MainActivity.all_events_data.get (i));
                    checkIfInside.add (MainActivity.all_events_data.get (i).getName ());
                    flag = false;
                }

                if (flag && MainActivity.all_events_data.get (i).getFilterName ().toLowerCase ().contains (search.toLowerCase ())) {
                    ans.add (MainActivity.all_events_data.get (i));
                    checkIfInside.add (MainActivity.all_events_data.get (i).getName ());
                    flag = false;
                }

                if (flag && MainActivity.all_events_data.get (i).getTags ().toLowerCase ().contains (search.toLowerCase ())) {
                    ans.add (MainActivity.all_events_data.get (i));
                    checkIfInside.add (MainActivity.all_events_data.get (i).getName ());
                }
                flag = true;
            }
        }
        return ans;
    }

    private void clearHistory() {
        history.clear ();
        File inputFile = new File ("history");
        File tempFile = new File ("myTempFile");
        tempFile.renameTo (inputFile);
    }

    private void readHistory() {
        FileInputStream inputStream;
        String readLine = "";
        int index = 0;
        try {
            inputStream = openFileInput ("history.txt");
            InputStreamReader inputStreamReader = new InputStreamReader (inputStream);
            BufferedReader bufferedReader = new BufferedReader (inputStreamReader);
            while ((readLine = bufferedReader.readLine ()) != null) {
                history.add (index++, readLine);
            }
            inputStream.close ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    private void saveHistory() {

        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput ("history.txt", Context.MODE_PRIVATE);
            for (int i = 0; i < history.size (); i++) {
                outputStream.write (history.get (i).getBytes ());
            }
            outputStream.close ();
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    @Override
    public void onBackPressed() {
        if (history.size () > 0) {
            saveHistory ();
        }
        startActivity (new Intent (this, MainActivity.class));
    }

}
