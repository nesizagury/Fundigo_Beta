package com.example.FundigoApp.Chat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.example.FundigoApp.R;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends BaseAdapter {

    private Context context;
    private List<MessageChat> allMessagesList = null;
    boolean isRealTime;

    public MessageAdapter(Context context, List<MessageChat> list, boolean isRealTime) {
        super ();
        this.context = context;
        this.allMessagesList = list;
        this.isRealTime = isRealTime;
    }

    @Override
    public int getCount() {
        return allMessagesList != null ? allMessagesList.size () : 0;
    }

    @Override
    public Object getItem(int position) {
        return allMessagesList.get (position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return this.allMessagesList.get (position).getIsSend () ? 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @SuppressLint("InflateParams")
    public View getView(int position, View convertView, ViewGroup parent) {

        final MessageChat messageChat = allMessagesList.get(position);
        boolean isSend = messageChat.getIsSend();

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder ();
            if (isSend) {
                convertView = LayoutInflater.from (context).inflate (R.layout.msg_item_right, null);
            } else {
                convertView = LayoutInflater.from (context).inflate (R.layout.msg_item_left, null);
            }
            viewHolder.sendDateTextView = (TextView) convertView.findViewById (R.id.sendDateTextView);
            viewHolder.sendTimeTextView = (TextView) convertView.findViewById (R.id.sendTimeTextView);
            viewHolder.textTextView = (TextView) convertView.findViewById (R.id.textTextView);

            viewHolder.isSend = isSend;
            convertView.setTag (viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag ();
        }

        try {
            String dateString = DateFormat.format ("dd-MM-yyyy h:mmaa", messageChat.getTime ()).toString ();
            String[] time = dateString.split (" ");
            viewHolder.sendDateTextView.setText (time[0]);
            viewHolder.sendTimeTextView.setText (time[1]);

            if (position == 0) {
                viewHolder.sendDateTextView.setVisibility (View.VISIBLE);
            } else {
                MessageChat previousMessage = allMessagesList.get (position - 1);
                if (inSameDay (previousMessage.getTime (), messageChat.getTime ())) {
                    viewHolder.sendDateTextView.setVisibility (View.GONE);
                } else {
                    viewHolder.sendDateTextView.setVisibility (View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace ();
        }

        switch (messageChat.getType ()) {
            case 0://text
                viewHolder.textTextView.setText (messageChat.getContent ());
                viewHolder.textTextView.setVisibility (View.VISIBLE);
                if (messageChat.getIsSend ()) {
                    LayoutParams sendTimeTextViewLayoutParams = (LayoutParams) viewHolder.sendTimeTextView.getLayoutParams ();
                    sendTimeTextViewLayoutParams.addRule (RelativeLayout.LEFT_OF, R.id.textTextView);
                    if (!isRealTime) {
                        viewHolder.sendTimeTextView.setLayoutParams (sendTimeTextViewLayoutParams);
                    } else {
                        viewHolder.sendTimeTextView.setVisibility (View.INVISIBLE);
                    }
                } else {
                    LayoutParams sendTimeTextViewLayoutParams = (LayoutParams) viewHolder.sendTimeTextView.getLayoutParams ();
                    sendTimeTextViewLayoutParams.addRule (RelativeLayout.RIGHT_OF, R.id.textTextView);
                    viewHolder.sendTimeTextView.setLayoutParams (sendTimeTextViewLayoutParams);
                    if (isRealTime) {
                        viewHolder.sendTimeTextView.setText (messageChat.getFromUserName ());
                    }
                }
                break;

            default:
                viewHolder.textTextView.setText (messageChat.getContent ());
                break;
        }

        return convertView;
    }

    public static boolean inSameDay(Date date1, Date Date2) {
        Calendar calendar = Calendar.getInstance ();
        calendar.setTime(date1);
        int year1 = calendar.get (Calendar.YEAR);
        int day1 = calendar.get (Calendar.DAY_OF_YEAR);

        calendar.setTime (Date2);
        int year2 = calendar.get (Calendar.YEAR);
        int day2 = calendar.get (Calendar.DAY_OF_YEAR);
        if ((year1 == year2) && (day1 == day2)) {
            return true;
        }
        return false;
    }

    static class ViewHolder {
        public TextView sendDateTextView;
        public TextView textTextView;
        public TextView sendTimeTextView;
        public boolean isSend = true;
    }


}
