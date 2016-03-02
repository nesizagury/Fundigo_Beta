package com.example.FundigoApp.Tickets;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.FundigoApp.R;

import java.util.List;

public class TicketAdapter extends ArrayAdapter<EventsSeats> {
    private static final String TAG = "TicketAdapter";
    Context context;
    List<EventsSeats> tickets;


    public TicketAdapter(Context context, List<EventsSeats> tickets) {
        super(context, 0, tickets);
        this.context = context;
        this.tickets = tickets;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final EventsSeats ticket = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.ticket_item, null);

            final ViewHolder holder = new ViewHolder();
            holder.tv_price = (TextView) convertView.findViewById(R.id.ticketItem_tv_price);
            holder.tv_ticket = (TextView) convertView.findViewById(R.id.ticketItem_tv_ticket);
            holder.tv_sold = (TextView) convertView.findViewById(R.id.ticketItem_tv_sold);

            convertView.setTag(holder);
        }

        final ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.tv_price.setText(ticket.getPrice() + "₪");
        holder.tv_ticket.setText(ticket.getSeatNumber());
        return convertView;
    }

    final class ViewHolder {
        public TextView tv_price;
        public TextView tv_ticket;
        public TextView tv_sold;
    }
}
