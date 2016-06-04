package com.ihxsf.gtd;

import android.app.Application;
import android.location.Location;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by hxsf on 16－05－25.
 */
public class GTDApplication extends Application {
    private Date lastGetLocation;
    private String ezLocation;
    private BDLocation location;

    @Override
    public void onCreate() {
        super.onCreate();
        // The realm file will be located in Context.getFilesDir() with name "default.realm"
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
        Log.i("realm", "set default");
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).build())
                        .build());
    }


    public Date getLastGetLocation() {
        return lastGetLocation;
    }

    public String getEzLocation() {
        return ezLocation;
    }

    public void setLastGetLocation(Date lastGetLocation) {
        this.lastGetLocation = lastGetLocation;
    }

    public void setEzLocation(String ezLocation) {
        this.ezLocation = ezLocation;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public BDLocation getLocation() {
        return location;
    }

    public void setLocation(BDLocation location) {
        this.location = location;
    }
}
