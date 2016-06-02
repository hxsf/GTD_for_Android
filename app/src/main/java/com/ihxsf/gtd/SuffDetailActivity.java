package com.ihxsf.gtd;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.ihxsf.gtd.data.Projects;
import com.ihxsf.gtd.data.Suff;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.*;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

public class SuffDetailActivity extends AppCompatActivity {
    private Realm realm;
    EditText e_title;
    EditText e_project;
    EditText e_desc;
    EditText e_datetime;
    EditText e_location;
    EditText e_tags;



    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            ((GTDApplication)getApplication()).setLocation(location);
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");

            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
            Log.i("BaiduLocationApiDem", sb.toString());
        }
    };

    private int isedit = 0x00000000;
    private final int editTitle = 0x00000001;
    private final int editDesc = 0x00000010;
    private final int editTime = 0x00000100;
    private final int editLoca = 0x00001000;
    private final int editTags = 0x00010000;
    Calendar time = Calendar.getInstance();
    private Suff suff;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
            time.set(Calendar.HOUR_OF_DAY, hourOfDay);
            time.set(Calendar.MINUTE, minute);
            e_datetime.setText(sdf.format(time.getTime()));
        }
    };
    private OnDateSetListener onDateSetListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            time.set(year,monthOfYear,dayOfMonth);
            e_datetime.setText(sdf.format(time.getTime()));
            TimePickerDialog dpd = TimePickerDialog.newInstance(
                    onTimeSetListener,
                    time.get(Calendar.HOUR_OF_DAY),
                    time.get(Calendar.MINUTE),
                    true
            );
            dpd.show(getFragmentManager(), "To Pick A Time");
        }
    };
    private double lastLatitude;
    private double lastLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suff_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.nav_arrow_back);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        setSupportActionBar(toolbar);
        View actionBarButtons = LayoutInflater.from(this).inflate(R.layout.edit_event_custom_actionbar,
                new LinearLayout(this), false);
        ActionBar ac = getSupportActionBar();
        assert ac != null;
//        ac.setDisplayShowCustomEnabled(true);
//        ac.setCustomView(R.layout.edit_event_custom_actionbar);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(SuffDetailActivity.this)
                        .setMessage("确认不保存么？")
                        .setNegativeButton("容我三思", null)
                        .setPositiveButton("去意已决", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .show();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        realm = Realm.getDefaultInstance();

        int id = getIntent().getIntExtra("id", 0);

        if (id > 0) {
            realm.beginTransaction();
            suff = realm.where(Suff.class).equalTo("id", id).findFirst();
            realm.commitTransaction();
        } else {
            suff = new Suff();
            realm.beginTransaction();
            suff.setId(realm.where(Suff.class).max("id").intValue()+1);
            realm.commitTransaction();
            suff.setProject(Projects.getPostion(Projects.Inbox));
        }
        init_values(suff);
        init_location();
    }

    private void init_location() {
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    private void init_values(Suff suff) {
        e_title = (EditText) findViewById(R.id.title_edit);
        e_project = (EditText) findViewById(R.id.project_select);
        e_desc = (EditText) findViewById(R.id.desc_edit);
        e_datetime = (EditText) findViewById(R.id.date_select);
        e_location = (EditText) findViewById(R.id.location_select);
        e_tags = (EditText) findViewById(R.id.tags);

        if(suff.getTime() !=null){
            time.setTime(suff.getTime());
        }
        e_datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        onDateSetListener,
                        time.get(Calendar.YEAR),
                        time.get(Calendar.MONTH),
                        time.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "To Pick A Date");
            }
        });

        e_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuffDetailActivity.this, ChooseLocationActivity.class);
                startActivityForResult(intent, 124);
            }
        });

        e_title.setText(suff.getTitle());
        e_project.setText(Projects.getValue(suff.getProject()).toString());
        e_desc.setText(suff.getDesc());
        e_datetime.setText(suff.getTime()!=null?new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(suff.getTime()):"");
        e_location.setText(suff.getEzLocation());
        e_tags.setText(suff.getTags()!=null?suff.getTags().toString():"");
    }
    private void update_values(Suff suff){
        if (e_title.getText().toString() != "" && e_title.getText().toString() != suff.getTitle()){
            isedit &= editTitle;
        }if (e_desc.getText().toString() != suff.getDesc()){
            isedit &= editDesc;
        }
        if((isedit & editLoca) != 0){
            suff.setLocation(lastLatitude, lastLongitude);
        }
        if((isedit & editTags) != 0) {

        }
        if((isedit & editTime) != 0) {
            suff.setTime(time.getTime());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 124) {

            LatLng latLng = data.getBundleExtra("data").getParcelable("location");
            e_location.setText("");//TODO display ezlocation
            lastLatitude = latLng.latitude;
            lastLongitude = latLng.longitude;
            isedit &= editLoca;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        mLocationClient.stop();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.clear();
        menu.add(0, 1, 0, "save").setIcon(R.drawable.ic_save).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Toast.makeText(SuffDetailActivity.this, "save", Toast.LENGTH_SHORT).show();
                realm.beginTransaction();
                update_values(suff);
                realm.copyToRealmOrUpdate(suff);
                realm.commitTransaction();
                finish();
                break;
            default:;
        }
        return true;
    }
}
