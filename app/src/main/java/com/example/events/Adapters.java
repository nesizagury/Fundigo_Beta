package com.example.events;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;


public class Adapters extends BaseAdapter {


    List<EventInfo> list = new ArrayList<EventInfo> ();
    Context context;

    public Adapters(Context c) {

        this.context = c;

        Resources res = context.getResources ();
        String[] eventDate_list;
        String[] eventName_list;
        String[] eventTag_list;
        String[] eventPrice_list;
        String[] eventInfo_list;
        String[] eventPlace_list;

        eventName_list = res.getStringArray (R.array.eventNames);
        eventDate_list = res.getStringArray (R.array.eventDates);
        eventTag_list = res.getStringArray (R.array.eventTags);
        eventPrice_list = res.getStringArray (R.array.eventPrice);
        eventInfo_list = res.getStringArray (R.array.eventInfo);
        eventPlace_list = res.getStringArray (R.array.eventPlace);

        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 14; i++)
                list.add (new EventInfo (
                                                R.mipmap.pic0 + i,
                                                eventDate_list[i],
                                                eventName_list[i],
                                                eventTag_list[i],
                                                eventPrice_list[i],
                                                eventInfo_list[i],
                                                eventPlace_list[i]));
        }
    }

    public Adapters(Context c, ArrayList<Event> arrayList) {

        this.context = c;

        Resources res = context.getResources ();
        String[] eventDate_list;
        String[] eventName_list;
        String[] eventTag_list;
        String[] eventPrice_list;
        String[] eventInfo_list;
        String[] eventPlace_list;
        eventName_list = res.getStringArray (R.array.eventNames);
        eventDate_list = res.getStringArray (R.array.eventDates);
        eventTag_list = res.getStringArray (R.array.eventTags);
        eventPrice_list = res.getStringArray (R.array.eventPrice);
        eventInfo_list = res.getStringArray (R.array.eventInfo);
        eventPlace_list = res.getStringArray (R.array.eventPlace);

        List<EventInfo> ans = new ArrayList<EventInfo> ();

        for (int j = 0; j < 1; j++) {
            for (int i = 0; i < 15; i++)
                ans.add (new EventInfo (
                                               R.mipmap.pic0 + i,
                                               eventDate_list[i],
                                               eventName_list[i],
                                               eventTag_list[i],
                                               eventPrice_list[i],
                                               eventInfo_list[i],
                                               eventPlace_list[i]));
        }
        boolean flag = true;
        for (int i = 0; i < arrayList.size (); i++) {
            for (int j = 0; j < ans.size () && flag; j++) {
                if (ans.get (j).getName ().equals (arrayList.get (i).getName ()) && flag) {
                    ans.get (j).setPlace (ans.get (j).getPlace () + " " + arrayList.get (i).getdis () + " km away");
                    list.add (ans.get (j));
                    flag = false;
                }
            }
            flag = true;
        }
    }


    @Override
    public int getCount() {
        return list.size ();
    }

    @Override
    public Object getItem(int i) {
        return list.get (i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        Holder holder;


        if (row == null) {
            LayoutInflater inflator = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            row = inflator.inflate (R.layout.list_view, viewGroup, false);
            holder = new Holder (row);
            row.setTag (holder);

        } else {
            holder = (Holder) row.getTag ();
        }

        EventInfo event = list.get (i);
        holder.image.setImageResource (event.imageId);

        holder.date.setText (event.getDate ());
        holder.name.setText (event.getName ());
        holder.tags.setText (event.getTags ());
        holder.price.setText (event.getPrice ());
        holder.place.setText (event.getPlace ());

        return row;
    }


}
