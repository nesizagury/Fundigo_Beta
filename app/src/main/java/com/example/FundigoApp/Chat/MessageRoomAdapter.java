package com.example.FundigoApp.Chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.FundigoApp.R;
import com.example.FundigoApp.StaticMethod.FileAndImageMethods;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MessageRoomAdapter extends BaseAdapter {

    List<MessageRoomBean> listOfConversations = new ArrayList<MessageRoomBean> ();
    ArrayList<String> listOfEventsImage = new ArrayList<> ();
    Context context;
    Boolean isCustomer = false;
    ImageLoader loader;

    public MessageRoomAdapter(Context c, List listOfConversations) {
        this.context = c;
        this.listOfConversations = listOfConversations;
        loader = FileAndImageMethods.getImageLoader (c);
    }

    public MessageRoomAdapter(Context c, List listOfConversations, ArrayList<String> listOfEventsImage) {
        this.context = c;
        this.listOfConversations = listOfConversations;
        this.listOfEventsImage = listOfEventsImage;
        isCustomer = true;
        loader = FileAndImageMethods.getImageLoader (c);
    }

    @Override
    public int getCount() {
        return listOfConversations.size ();
    }

    @Override
    public Object getItem(int i) {
        return listOfConversations.get (i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View row = view;
        MessageRoomItemHolder holder = null;

        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate (R.layout.messages_room_item, viewGroup, false);
            holder = new MessageRoomItemHolder (row);
            row.setTag (holder);

        } else {
            holder = (MessageRoomItemHolder) row.getTag ();
        }
        MessageRoomBean message_bean = listOfConversations.get (i);
        if (isCustomer) {
            loader.displayImage (listOfEventsImage.get (i), holder.customerOrEventImage);
        } else if (message_bean.getCustomerImageFacebookUrl () != null &&
                           !message_bean.getCustomerImageFacebookUrl ().isEmpty ()) {
            Picasso.with (context).load (message_bean.getCustomerImageFacebookUrl ()).into (holder.customerOrEventImage);
        } else if (message_bean.getCustomerImage () != null) {
            loader.displayImage (message_bean.getCustomerImage (), holder.customerOrEventImage);
        }
        holder.messageBody.setText (message_bean.getLastMessage ());
        holder.customerOrEventName.setText (message_bean.getCustomer_id ());
        holder.customerOrEventImage.setTag (message_bean);
        holder.customerOrEventName.setTag (message_bean);
        holder.messageBody.setTag (message_bean);

        return row;
    }
}
