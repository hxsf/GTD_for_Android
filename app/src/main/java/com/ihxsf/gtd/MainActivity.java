package com.ihxsf.gtd;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.slider.LightnessSlider;
import com.ihxsf.gtd.View.SuffDividerItemDecoration;
import com.ihxsf.gtd.View.SuffDividerLine;
import com.ihxsf.gtd.View.SuffItemTouchHelper;
import com.ihxsf.gtd.View.SuffListAdapter;
import com.ihxsf.gtd.data.Projects;
import com.ihxsf.gtd.data.Suff;
import com.ihxsf.gtd.data.Tag;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.BaseViewHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.ArrayList;
import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private Realm realm;
    private RecyclerView recyclerView;
    private OrderedRealmCollection<Suff> list;
    private Drawer drawer;
    private Toolbar toolbar;
    private PrimaryDrawerItem tagsDrawerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        testPermission();
        realm = Realm.getDefaultInstance();
        recyclerView = (RecyclerView) findViewById(R.id.suff_list);
        setUpRecyclerView();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SuffDetailActivity.class);
                intent.putExtra("id", 0);
                startActivity(intent);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        drawer = init_drawer(savedInstanceState);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
    }

    private Drawer init_drawer(Bundle savedInstanceState) {
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withHeaderBackground(R.drawable.side_nav_bar)
                .withSavedInstance(savedInstanceState)
                .build();
        realm.beginTransaction();
        RealmResults<Tag> tags = realm.where(Tag.class).findAll();
        realm.commitTransaction();
        List<IDrawerItem> tags_list= new ArrayList<>();

        for (Tag tag : tags) {
            tags_list.add(new SecondaryDrawerItem()
                    .withName(tag.getName())
                    .withIcon(R.drawable.ic_menu_item_cicle)
                    .withIconColor((int) tag.getColor())
                    .withSelectedIconColor((int) tag.getColor())
                    .withLevel(2)
                    .withIconTintingEnabled(true).withIdentifier(tag.getId()+10)
            );
        }
        tagsDrawerItem = new PrimaryDrawerItem().withName("Tags").withIdentifier(0).withSubItems(tags_list).withIsExpanded(true).withSelectable(false);

        Drawer.OnDrawerItemClickListener drawerItemClickHandle = new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                Log.i("Item", "postion="+position+" id="+drawerItem.getIdentifier());
                long id = drawerItem.getIdentifier();

                if (id == 10) {
                    View dialogview = getLayoutInflater().inflate(R.layout.dialog_add_tag, null);
                    final EditText tagname = (EditText) dialogview.findViewById(R.id.tag_name);
                    final ColorPickerView colorPickerView = (ColorPickerView) dialogview.findViewById(R.id.color_picker_view);
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Add A New Tag")
                            .setView(dialogview)
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    realm.beginTransaction();
                                    Tag tag = new Tag();
                                    int id = realm.where(Tag.class).max("id").intValue()+1;
                                    tag.setId(id);
                                    String name = tagname.getText().toString();
                                    int color = colorPickerView.getSelectedColor();
                                    if (realm.where(Tag.class).equalTo("name", name).count() > 0){
                                        new AlertDialog.Builder(MainActivity.this).setTitle("Tag已存在").setPositiveButton("OK", null).show();
                                        realm.cancelTransaction();
                                    } else {
                                        tag.setName(name);
                                        tag.setColor(color);
                                        tagsDrawerItem.getSubItems().add(
                                                new SecondaryDrawerItem()
                                                        .withName(name)
                                                        .withIcon(R.drawable.ic_menu_item_cicle)
                                                        .withIconColor(color)
                                                        .withSelectedIconColor(color)
                                                        .withLevel(2)
                                                        .withIconTintingEnabled(true).withIdentifier(id+10)

                                        );
                                        realm.copyToRealmOrUpdate(tag);
                                        drawer.getAdapter().notifyAdapterSubItemsChanged(drawer.getPosition(0));
                                        realm.commitTransaction();
                                    }
                                }
                            }).show();
                }else {
                    if (id == 1) {
                        list = realm.where(Suff.class).equalTo("project", Projects.getPostion(Projects.Inbox)).findAll();
                    } else if (id == 2) {
                        list = realm.where(Suff.class).equalTo("project", Projects.getPostion(Projects.Next)).findAll();
                    } else if (id == 3) {
                        list = realm.where(Suff.class).equalTo("project", Projects.getPostion(Projects.Watch)).findAll();
                    } else if (id == 4) {
                        list = realm.where(Suff.class).equalTo("project", Projects.getPostion(Projects.Future)).findAll();
                    } else if (id > 10) {
                        list = realm.where(Suff.class).equalTo("tags.id", id-10).findAll();
                    }
                    recyclerView.setAdapter(new SuffListAdapter(MainActivity.this, list));
                }
                return true;
            }
        };
        Drawer.OnDrawerItemLongClickListener drawerItemLongClickHandle = new Drawer.OnDrawerItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view, int position, final IDrawerItem drawerItem) {
                final long id = drawerItem.getIdentifier();
                if (id > 10) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete This Tag")
                            .setNegativeButton("Cancel", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    realm.executeTransaction(new Realm.Transaction() {
                                        @Override
                                        public void execute(Realm realm) {
                                            realm.where(Tag.class).equalTo("id", id-10).findFirst().deleteFromRealm();
                                        }
                                    });
                                    tagsDrawerItem.getSubItems().remove(drawerItem);
                                    drawer.getAdapter().notifyAdapterSubItemsChanged(drawer.getPosition(0));
                                }
                            }).show();
                    return true;
                }
                return false;
            }
        };
        return new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withScrollToTopAfterClick(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Inbox").withIdentifier(1).withIcon(R.drawable.ic_menu_inbox),
                        new PrimaryDrawerItem().withName("Next").withIdentifier(2).withIcon(R.drawable.ic_menu_next),
                        new PrimaryDrawerItem().withName("Watch").withIdentifier(3).withIcon(R.drawable.ic_menu_watch),
                        new PrimaryDrawerItem().withName("Futrue").withIdentifier(4).withIcon(R.drawable.ic_menu_future),
                        tagsDrawerItem,
                        new PrimaryDrawerItem().withName("Add New Tag").withIdentifier(10).withIcon(R.drawable.ic_add).withSelectable(false).withIconTintingEnabled(true).withIconColor(Color.parseColor("#000000"))
                )
                .withOnDrawerItemClickListener(drawerItemClickHandle)
                .withOnDrawerItemLongClickListener(drawerItemLongClickHandle)
                .build();
    }

    private void testPermission() {
        PackageManager pm = this.getPackageManager();
        PackageInfo pi;
        try {
            // 参数2必须是PackageManager.GET_PERMISSIONS
            pi = pm.getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = pi.requestedPermissions;
            if(permissions != null){
                for(String str : permissions){
                    Log.i("permission", str);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = realm.where(Suff.class).findAllAsync();
        SuffListAdapter adapter = new SuffListAdapter(this, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SuffDividerItemDecoration(this, SuffDividerItemDecoration.VERTICAL_LIST));
        ItemTouchHelper.Callback callback =
                new SuffItemTouchHelper(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(recyclerView);
    }

    public void deleteItem(Suff item) {
        final int id = item.getId();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Suff.class).equalTo("id", id)
                        .findFirst()
                        .deleteFromRealm();
            }
        });
    }
    public void deleteItem(int postion) {
        realm.beginTransaction();
        list.deleteFromRealm(postion);
        realm.commitTransaction();
    }
//
//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(MenuItem item) {
//        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//            list = realm.where(Suff.class).equalTo("project", Projects.getPostion(Projects.Inbox)).findAll();
//        } else if (id == R.id.nav_gallery) {
//            list = realm.where(Suff.class).equalTo("project", Projects.getPostion(Projects.Next)).findAll();
//        } else if (id == R.id.nav_slideshow) {
//            list = realm.where(Suff.class).equalTo("project", Projects.getPostion(Projects.Watch)).findAll();
//        } else if (id == R.id.nav_manage) {
//            list = realm.where(Suff.class).equalTo("project", Projects.getPostion(Projects.Future)).findAll();
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }
//        Log.i("list size", list.size()+"");
//        recyclerView.setAdapter(new SuffListAdapter(MainActivity.this, list));
//
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }
}
