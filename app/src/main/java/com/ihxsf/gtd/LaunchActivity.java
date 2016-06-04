package com.ihxsf.gtd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.kayvannj.permission_utils.Func;
import com.github.kayvannj.permission_utils.PermissionUtil;
import com.ihxsf.gtd.data.Suff;
import com.ihxsf.gtd.data.Tag;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

public class LaunchActivity extends AppCompatActivity {

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
    private PermissionUtil.PermissionRequestObject mRequestObject;

    private List<String> permissions = new ArrayList<String>();
    private int permission_num = 0;
    private boolean cango = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        AsyncTask asyncTask = new AsyncTask() {
            private Intent intent;
            public static final long LAUNCH_TIME = 1000;

            @Override
            protected void onPreExecute() {
                intent = new Intent(LaunchActivity.this, MainActivity.class);
                super.onPreExecute();
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
                    init_permission();
                    insert_test_data();
                    while (!cango) {
                        Thread.sleep(LAUNCH_TIME);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
        asyncTask.execute();
    }

    private void insert_test_data() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        System.out.println("start init data");

        Tag tag1 = new Tag();
        tag1.setId(1);
        tag1.setName("生活");
        tag1.setColor(Color.parseColor("#ff0000"));
        tag1 = realm.copyToRealmOrUpdate(tag1);

        Tag tag2 = new Tag();
        tag2.setId(2);
        tag2.setName("项目");
        tag2.setColor(Color.parseColor("#00ff00"));
        tag2 = realm.copyToRealmOrUpdate(tag2);

        Suff suff0 = new Suff(); // Create a new object
        suff0.setTitle("Top TODO");
        suff0.setId(1);
        suff0.setProject(1);
        suff0.setTime(new Date(System.currentTimeMillis()+1000000));
        suff0 = realm.copyToRealmOrUpdate(suff0);
        suff0.getTags().add(tag1);
        suff0.getTags().add(tag2);
        suff0 = realm.copyToRealmOrUpdate(suff0);

        Suff suff1 = new Suff(); // Create a new object
        suff1.setTitle("1st TODO");
        suff1.setId(2);
        suff1.setProject(1);
        suff1.setTime(new Date(System.currentTimeMillis()+50000000));
        suff1 = realm.copyToRealmOrUpdate(suff1);
        suff1.getTags().add(tag1);
        suff1 = realm.copyToRealmOrUpdate(suff1);

        Suff suff2 = new Suff(); // Create a new object
        suff2.setTitle("2nd TODO");
        suff2.setId(3);
        suff2.setProject(2);
        suff2.setTime(new Date(System.currentTimeMillis()+20000000));
        suff2 = realm.copyToRealmOrUpdate(suff2);
        suff2.getTags().add(tag2);
        suff2 = realm.copyToRealmOrUpdate(suff2);

        realm.commitTransaction();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permission_num--;
        if (permission_num == 0){
            cango = true;
        }
    }

    private void init_permission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_WIFI_STATE);
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            }
            if (checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.CHANGE_WIFI_STATE);
            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
//            if (checkSelfPermission(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS) != PackageManager.PERMISSION_GRANTED){
//                permissions.add(Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS);
//            }
            permission_num = permissions.size();
            if (permission_num > 0) {
                cango = false;
                mRequestObject = PermissionUtil.with(this).request(permissions.toArray(new String[permissions.size()]))
                        .onAllGranted(new Func() {
                            @Override
                            protected void call() {
                                cango = true;
                            }
                        }).onAnyDenied(new Func() {
                            @Override
                            protected void call() {
                                cango = true;
                            }
                        }).ask(REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            } else {
                cango = true;
            }
        }

    }


}
