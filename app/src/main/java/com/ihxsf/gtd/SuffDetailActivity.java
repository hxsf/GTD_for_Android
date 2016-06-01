package com.ihxsf.gtd;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ihxsf.gtd.data.Projects;
import com.ihxsf.gtd.data.Suff;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog.*;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        if((isedit & editTitle) != 0){
            suff.setTitle(e_title.getText().toString());
        }
        if((isedit & editDesc) != 0) {
            suff.setDesc(e_desc.getText().toString());
        }
        if((isedit & editTime) != 0) {
            suff.setTime(time.getTime());
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
