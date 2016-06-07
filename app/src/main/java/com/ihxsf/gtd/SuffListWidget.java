package com.ihxsf.gtd;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class SuffListWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = updateWidgetListView(context, appWidgetId);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list);
    }

    private static RemoteViews updateWidgetListView(Context context, int appWidgetId) {
        //which layout to show on widget
        RemoteViews remoteViews = new RemoteViews(
                context.getPackageName(), R.layout.suff_list_widget);

        //RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, WidgetService.class);
        //passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //setting a unique Uri to the intent
        //don't know its purpose to me right now
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
        //setting adapter to listview of the widget
        remoteViews.setRemoteAdapter(R.id.widget_list, svcIntent);
        Intent intent = new Intent(context, SuffDetailActivity.class);
        intent.putExtra("type", 0);//new suff
        intent.putExtra("id", 0);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.widget_add, pendingIntent);
        remoteViews.setOnClickPendingIntent(R.id.logo, PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0));
        Intent intent2 = new Intent(context, SuffDetailActivity.class);
        intent2.setAction("com.ihxsf.gtd.event.COLLECTION_VIEW_ACTION");
        intent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent pendingIntentTemplate = PendingIntent.getActivity(context, 1, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setPendingIntentTemplate(R.id.widget_list, pendingIntentTemplate);

        //setting an empty view in case of no data
//        remoteViews.setEmptyView(R.id.widget_list, R.id.empty_view);
        return remoteViews;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals("com.ihxsf.gtd.event.COLLECTION_VIEW_ACTION")) {
            Log.i("list_item_click", intent.getIntExtra("id", -1)+"");
        }
    }
}

