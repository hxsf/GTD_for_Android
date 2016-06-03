package com.ihxsf.gtd;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.model.LatLng;
import com.ihxsf.gtd.View.TagsAutoCompleteView;
import com.ihxsf.gtd.data.Projects;
import com.ihxsf.gtd.data.Suff;
import com.ihxsf.gtd.data.Tag;
import com.tokenautocomplete.FilteredArrayAdapter;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.*;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class SuffDetailActivity extends AppCompatActivity {
    private Realm realm;
    EditText e_title;
    EditText e_project;
    EditText e_desc;
    EditText e_datetime;
    EditText e_location;
    TagsAutoCompleteView e_tags;

    private double lastLatitude;
    private double lastLongitude;
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
            isedit |= editTime;
        }
    };
    private OnDateSetListener onDateSetListener = new OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
            time.set(year,monthOfYear,dayOfMonth);
            e_datetime.setText(sdf.format(time.getTime()));
            isedit |= editTime;
            TimePickerDialog dpd = TimePickerDialog.newInstance(
                    onTimeSetListener,
                    time.get(Calendar.HOUR_OF_DAY),
                    time.get(Calendar.MINUTE),
                    true
            );
            dpd.show(getFragmentManager(), "To Pick A Time");
        }
    };
    private String ezLocation;
    private AlertDialog.Builder nullTitleDialog = null;
    private AlertDialog.Builder goBackDialog = null;
    private DatePickerDialog datePickerDialog = null;

    private void initDialog() {
        nullTitleDialog = new AlertDialog.Builder(SuffDetailActivity.this)
                .setMessage("Can't Add Suff Without Title")
                .setPositiveButton("OK", null);
        goBackDialog = new AlertDialog.Builder(SuffDetailActivity.this)
                .setMessage(getString(R.string.go_back_without_save))
                .setNegativeButton(getString(R.string.cancel_go_back), null)
                .setPositiveButton(getString(R.string.goback), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        datePickerDialog = DatePickerDialog.newInstance(
                onDateSetListener,
                time.get(Calendar.YEAR),
                time.get(Calendar.MONTH),
                time.get(Calendar.DAY_OF_MONTH)
                );
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suff_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        assert toolbar != null;
        toolbar.setNavigationIcon(R.drawable.nav_arrow_back);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return false;
            }
        });
        setSupportActionBar(toolbar);
        ActionBar ac = getSupportActionBar();
        assert ac != null;
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                can_go_back();
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
        initDialog();
    }
    private void can_go_back(){
        if (check_isedited()){
            goBackDialog.show();
        } else {
            finish();
        }
    }

    private void init_values(Suff suff) {
        e_title = (EditText) findViewById(R.id.title_edit);
        e_project = (EditText) findViewById(R.id.project_select);
        e_desc = (EditText) findViewById(R.id.desc_edit);
        e_datetime = (EditText) findViewById(R.id.date_select);
        e_location = (EditText) findViewById(R.id.location_select);
        e_tags = (TagsAutoCompleteView) findViewById(R.id.tags);

        realm.beginTransaction();
        RealmResults<Tag> tags = realm.where(Tag.class).findAll();
        realm.commitTransaction();

        List<Tag> list = new ArrayList<>();
        for (Tag tag : tags) {
            list.add(new Tag(tag));
        }

        e_tags.setAdapter(new FilteredArrayAdapter<Tag>(this, android.R.layout.simple_list_item_1, list) {
            @Override
            protected boolean keepObject(Tag obj, String mask) {
                return obj.getName().startsWith(mask);
            }
        });

        if(suff.getTime() !=null){
            time.setTime(suff.getTime());
        }
        e_datetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show(getFragmentManager(), "To Pick A Date");
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
    private boolean check_isedited () {
        if ((!e_title.getText().toString().equals(suff.getTitle()!=null?suff.getTitle():""))){
            isedit |= editTitle;
        }
        if (!e_desc.getText().toString().equals(suff.getDesc()!=null?suff.getDesc():"")){
            isedit |= editDesc;
        }
        return isedit != 0;
    }
    private void update_values(Suff suff){
        check_isedited();
        if ((isedit & editTitle) != 0){
            suff.setTitle(e_title.getText().toString());
        }
        if ((isedit & editDesc) != 0){
            suff.setDesc(e_desc.getText().toString());
        }
        if((isedit & editLoca) != 0){
            suff.setLocation(lastLatitude, lastLongitude);
            suff.setEzLocation(ezLocation);
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
            Bundle bundle = data.getBundleExtra("data");
            LatLng latLng = bundle.getParcelable("location");
            ezLocation = bundle.getString("ezlocation");
            e_location.setText(ezLocation);
            assert latLng != null;
            lastLatitude = latLng.latitude;
            lastLongitude = latLng.longitude;
            isedit |= editLoca;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK ) {
            can_go_back();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
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
                if (e_title.getText().toString().equals("")) {
                    nullTitleDialog.show();
                } else {
                    realm.beginTransaction();
                    update_values(suff);
                    realm.copyToRealmOrUpdate(suff);
                    realm.commitTransaction();
                    finish();
                }
                break;
            default:
        }
        return true;
    }
}
