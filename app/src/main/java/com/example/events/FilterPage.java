package com.example.events;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

public class FilterPage extends AppCompatActivity implements   AdapterView.OnItemClickListener,Serializable
{
    /** Variable Decleration */
    Integer[] Images = {
                               R.drawable.ic_sport, R.drawable.ic_airplane,
                               R.drawable.ic_beer, R.drawable.ic_buisness,
                               R.drawable.ic_camera, R.drawable.ic_education,
                               R.drawable.ic_gov, R.drawable.ic_home,
                               R.drawable.ic_music
    };
    private final String[] Names = {
                                           "Sports", "Travel",
                                           "Drink", "Business",
                                           "Fashion", "Education",
                                           "Government" ,"Home & LifeStyle",
                                           "Music"
    };

    private  String ans;
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_page);
        setTitle("FilterPage");

        /** GridView gridview = (GridView) findViewById(R.id.grdSports);
         gridview.setAdapter(new ImageAdapter(this, Names, Images)); */

        /** Create the Custom Grid View*/
        ImageAdapter adapter = new ImageAdapter(FilterPage.this, Names, Images);
        gridView = (GridView) findViewById(R.id.grdFilter);
        gridView.setAdapter(adapter);

        /**  On click Event when the custom grid is clickes*/
        gridView.setOnItemClickListener(this);

    }



    @Override
    public void onItemClick(AdapterView<?> av, View view, int i, long l) {
        if(ans==null) {
            ans = Names[i];
            view.setBackgroundColor(Color.RED);
        }else{
            if(ans.equals(Names[i]))
            {
                ans=null;
                view.setBackgroundColor(Color.TRANSPARENT);
            }else{
                Toast.makeText(this,"Can Choice One Category",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                    && keyCode == KeyEvent.KEYCODE_BACK
                    && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



    @Override
    public void onBackPressed() {

        ArrayList<EventInfo> arr = new ArrayList<EventInfo>();
        if (ans != null)
        {
            for (int i = 0; i < 15; i++)
            {
                if (ans.equals(MainActivity.all_events_data.get(i).getFilterName()) && !arr.contains(MainActivity.all_events_data.get(i)))
                {
                    arr.add(MainActivity.all_events_data.get(i));
                }
            }
            Intent newIntent = new Intent(this, MainActivity.class);
            newIntent.putExtra("List", arr);
            startActivity(newIntent);
        }else{
            startActivity(new Intent(this,MainActivity.class));
        }
    }

}