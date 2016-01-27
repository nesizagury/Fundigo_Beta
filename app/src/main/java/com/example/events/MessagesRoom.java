package com.example.events;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MessagesRoom extends Activity implements AdapterView.OnItemClickListener {

    ListView list_view;
    List<Room> list = new ArrayList<Room> ();
    List<MessageRoomBean> mrbList = new ArrayList<MessageRoomBean> ();
    MessageRoomAdapter mra;
    String producer_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_messages_room);

        list_view = (ListView) findViewById (R.id.listView);
        Intent intent = getIntent ();
        mrbList = (ArrayList<MessageRoomBean>) getIntent ().getSerializableExtra ("array");
        producer_id = intent.getStringExtra ("producer_id");

        mra = new MessageRoomAdapter (this, mrbList);
        list_view.setAdapter (mra);
        list_view.setOnItemClickListener (this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent (this, ChatActivity.class);
        MessageItemHolder holder = (MessageItemHolder) view.getTag ();
        MessageRoomBean mrb = (MessageRoomBean) holder.customer.getTag ();
        intent.putExtra ("producer_id", producer_id);
        intent.putExtra ("customer_id", mrb.customer_id);

        startActivity (intent);
    }


}
