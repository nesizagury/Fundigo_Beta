package com.example.FundigoApp;

import android.app.Application;

import com.example.FundigoApp.Chat.Message;
import com.example.FundigoApp.Chat.MessageToCustomer;
import com.example.FundigoApp.Chat.MsgRealTime;
import com.example.FundigoApp.Chat.Room;
import com.example.FundigoApp.Customer.CustomerMenu.CreditCard;
import com.example.FundigoApp.Customer.Social.Profile;
import com.example.FundigoApp.Events.Event;
import com.example.FundigoApp.Tickets.EventsSeats;
import com.example.FundigoApp.Tickets.SoldTickets;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import io.branch.referral.Branch;

@ReportsCrashes(formUri = "https://collector.tracepot.com/b30094f1")
public class Fundigo extends Application {
    @Override
    public void onCreate() {
        super.onCreate ();
        Branch.getInstance (this);
        Branch.getAutoInstance (this);
        ACRA.init (this);
        Parse.enableLocalDatastore (this);
        Parse.initialize (this);
        try {
            ParseInstallation.getCurrentInstallation ().save ();
        } catch (ParseException e) {
            e.printStackTrace ();
        }
        ParseInstallation.getCurrentInstallation ().getObjectId ();
        ParseObject.registerSubclass (Event.class);
        ParseObject.registerSubclass (Message.class);
        ParseObject.registerSubclass (Room.class);
        ParseObject.registerSubclass (MsgRealTime.class);
        ParseObject.registerSubclass (EventsSeats.class);
        ParseObject.registerSubclass (CreditCard.class);
        ParseObject.registerSubclass (Profile.class);
        ParseObject.registerSubclass (SoldTickets.class);
        ParseObject.registerSubclass (MessageToCustomer.class);
        FacebookSdk.sdkInitialize (getApplicationContext ());
        ParseUser.enableAutomaticUser ();
        ParseACL defaultAcl = new ParseACL ();
        defaultAcl.setPublicReadAccess (true);
        defaultAcl.setPublicWriteAccess (true);
        ParseACL.setDefaultACL (defaultAcl, true);
        AccessToken.refreshCurrentAccessTokenAsync ();
    }
}