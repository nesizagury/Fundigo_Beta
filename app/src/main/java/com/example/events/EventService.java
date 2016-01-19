package com.example.events;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by MAMO on 15/01/2016.
 */
public class EventService extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_service);
        ListView event_service_listView;
        DisplayMetrics ma =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(ma);



        int width = ma.widthPixels;
        int hieght= ma.heightPixels;

        getWindow().setLayout((int) (width * .7), (int) (hieght * .325));

        final Intent myintent = getIntent ();


        String arrTitles[] = getResources().getStringArray(R.array.eventServiceTitles);
        int [] images= new int[]{R.drawable.toilet_ldpi, R.drawable.parking_ldpi, R.drawable.crowd_ldpi, R.drawable.atm_icon_ldpi};
        String toilet=myintent.getStringExtra("toilet");
        String parking=myintent.getStringExtra("parking");
        String capacity=myintent.getStringExtra("capacity");
        String atm=myintent.getStringExtra("atm");

        event_service_listView=(ListView) findViewById(R.id.event_service_listView);
        servisAdapter adapter=new servisAdapter (this,arrTitles,images,toilet ,parking,capacity,atm);
        event_service_listView.setAdapter(adapter);
    }
}

class servisAdapter extends ArrayAdapter<String> {
    Context context;
    int[] images;
    String[] titaleArray;
    String toilet;
    String parking;
    String capacity;
    String atmt;

    servisAdapter(Context c, String[] titles, int ima[], String Toi, String Par, String Capa, String Atm) {
        super(c, R.layout.service_row, R.id.event_service_listView, titles);
        this.context = c;
        this.images = ima;
        this.titaleArray = titles;
        this.toilet = Toi;
        this.parking = Par;
        this.capacity = Capa;
        this.atmt = Atm;

    }

    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.service_row, parent, false);

        ImageView myImage = (ImageView) row.findViewById(R.id.imageView4);
        TextView myTaitleText = (TextView) row.findViewById(R.id.textView5);
        TextView myDescriText = (TextView) row.findViewById(R.id.textView6);

        myImage.setImageResource(images[position]);
        myTaitleText.setText(titaleArray[position]);

        switch (position) {
            case 0:
                myDescriText.setText(toilet);
                break;
            case 1:
                myDescriText.setText(parking);
                break;
            case 2:
                myDescriText.setText(capacity);
                break;
            case 3:
                myDescriText.setText(atmt);
                break;

        }


        return row;
    }


}
