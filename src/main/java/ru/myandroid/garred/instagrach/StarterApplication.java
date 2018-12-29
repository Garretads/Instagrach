package ru.myandroid.garred.instagrach;

import android.app.Application;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class StarterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId("e3939cd2bfcaab680d76579b306c642310ae06d8")
                .clientKey("37c0c3b6fe4ebd84de97037592fd7ee870a6e2e7")
                .server("http://35.180.63.246:80/parse/")
                .build());
        ParseObject object = new ParseObject("ExampleObject");
        object.put("MyString","123");
        object.put("Second","564");

        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) Log.i("Result: ", "Parse result successful");
                else Log.i("Result: ", "Parse failed");
            }
        });
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL,true);
    }
}
