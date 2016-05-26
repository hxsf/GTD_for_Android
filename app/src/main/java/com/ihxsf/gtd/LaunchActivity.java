package com.ihxsf.gtd;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ihxsf.gtd.data.Suff;

import java.util.Date;

import io.realm.Realm;

public class LaunchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

//
// TODO REMOVE
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//
//        Suff suff0 = realm.createObject(Suff.class); // Create a new object
//        suff0.setTitle("Top TODO");
//        suff0.setTime(new Date(System.currentTimeMillis()-1000));
////        suff1.setLocation(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//
//        Suff suff1 = realm.createObject(Suff.class); // Create a new object
//        suff1.setTitle("1st TODO");
//        suff1.setTime(new Date(System.currentTimeMillis()-1000));
////        suff1.setLocation(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//
//        Suff suff2 = realm.createObject(Suff.class); // Create a new object
//        suff2.setTitle("2nd TODO");
//        suff2.setTime(new Date(System.currentTimeMillis()));
////        suff2.setLocation(aMapLocation.getLatitude(), aMapLocation.getLongitude());
//
////        suff0.addChildSuff(suff1);
////        suff0.addChildSuff(suff2);
//
//        realm.commitTransaction();
//        //


        new AsyncTask() {
            public static final long LAUNCH_TIME = 1000;
            Intent intent;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                intent = new Intent(LaunchActivity.this, MainActivity.class);
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                startActivity(intent);
                finish();
            }

            @Override
            protected Object doInBackground(Object[] params) {
                try {
                    Thread.sleep(LAUNCH_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute();
    }

}
