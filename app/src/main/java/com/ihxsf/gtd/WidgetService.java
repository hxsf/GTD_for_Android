package com.ihxsf.gtd;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.ihxsf.gtd.data.Suff;
import com.ihxsf.gtd.util.DateCalc;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.realm.Realm;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    private static class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private List<Suff> widgetItems = new ArrayList<>();
        private Context context;
        private int widgetid;
        private Realm realm;
        private final BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals(Intent.ACTION_TIME_TICK)) {
                    context.sendBroadcast(new Intent("com.ihxsf.gtd.event.UPDATE_DATA"));
                }
            }
        };

        public ListRemoteViewsFactory(Context applicationContext, Intent intent) {
            context = applicationContext;
            widgetid = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {
            realm = Realm.getDefaultInstance();
            widgetItems = Suff.clone(realm.where(Suff.class).equalTo("isDone", false).findAll());
            realm.close();
            realm = null;

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_TIME_TICK);
            context.registerReceiver(receiver, filter);
        }

        @Override
        public void onDataSetChanged() {
            realm = Realm.getDefaultInstance();
            widgetItems = Suff.clone(realm.where(Suff.class).equalTo("isDone", false).findAll());
            realm.close();
            realm = null;
        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return widgetItems.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            final RemoteViews remoteView = new RemoteViews(
                    context.getPackageName(), R.layout.suff_item_main);
            Suff suff = widgetItems.get(position);
            remoteView.setTextViewText(R.id.item_title, suff.getTitle());
            remoteView.setTextViewText(R.id.item_time, DateCalc.fromToday(suff.getTime()));
            remoteView.setTextViewText(R.id.item_location, suff.getEzLocation());
            Intent intent = new Intent();
            intent.putExtra("type", 1);//modif
            intent.putExtra("id", suff.getId());
            remoteView.setOnClickFillInIntent(R.id.suff_item, intent);
            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
