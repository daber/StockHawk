package com.udacity.stockhawk.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.ui.DetailActivity;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Implementation of App Widget functionality.
 */
public class StockWidget extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        Intent dataIntent = new Intent(context, StockWidgetService.class).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        dataIntent.setData(Uri.parse(dataIntent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.stockView, dataIntent);
        views.setEmptyView(R.id.stockView, R.id.empty_view);
        Intent iMain = new Intent(context, MainActivity.class);
        PendingIntent piMain = PendingIntent.getActivity(context, 0, iMain, 0);
        views.setOnClickPendingIntent(R.id.widget_content, piMain);

        Intent iDetail = new Intent(context, DetailActivity.class);
        PendingIntent piDetail = TaskStackBuilder.create(context).addNextIntentWithParentStack(iDetail).getPendingIntent(0,0);

        views.setPendingIntentTemplate(R.id.stockView,piDetail);


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
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
}

