package com.ihxsf.gtd;

import android.app.Application;
import android.util.Log;

import com.yayandroid.locationmanager.LocationConfiguration;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hxsf on 16－05－25.
 */
public class GTDApplication extends Application {
    public LocationConfiguration locationConfiguration;
    private double longitude;
    private double latitude;
    private Date lastGetLocation;
    private String ezLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        // The realm file will be located in Context.getFilesDir() with name "default.realm"
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        Log.i("realm", "set default");
    }

    public LocationConfiguration getLocationConfiguration() {
        return locationConfiguration;
    }

    public void setLocationConfiguration(LocationConfiguration locationConfiguration) {
        this.locationConfiguration = locationConfiguration;
    }

    public Date getLastGetLocation() {
        return lastGetLocation;
    }

    public String getEzLocation() {
        return ezLocation;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
