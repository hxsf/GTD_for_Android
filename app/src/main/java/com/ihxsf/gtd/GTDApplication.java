package com.ihxsf.gtd;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hxsf on 16－05－25.
 */
public class GTDApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // The realm file will be located in Context.getFilesDir() with name "default.realm"
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        Log.i("realm", "set default");

    }
}
