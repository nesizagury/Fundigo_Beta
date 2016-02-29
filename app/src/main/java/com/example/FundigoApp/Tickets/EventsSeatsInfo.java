package com.example.FundigoApp.Tickets;

import android.graphics.Bitmap;

import com.example.FundigoApp.Events.EventInfo;

import java.util.Date;

public class EventsSeatsInfo {
    private String ticketName;
    private Bitmap QR;
    private Date purchaseDate;
    private int price;
    private EventInfo eventInfo;

    public EventsSeatsInfo(String ticketName,
                           Bitmap QR,
                           Date purchaseDate,
                           int price,
                           EventInfo eventInfo) {
        this.ticketName = ticketName;
        this.QR = QR;
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.eventInfo = eventInfo;
    }

    public String getTicketName() {
        return ticketName;
    }

    public Bitmap getQR() {
        return QR;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public int getPrice() {
        return price;
    }

    public EventInfo getEventInfo() {
        return eventInfo;
    }

    public void setEventInfo(EventInfo eventInfo) {
        this.eventInfo = eventInfo;
    }
}
