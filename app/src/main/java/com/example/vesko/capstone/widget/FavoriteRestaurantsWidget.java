package com.example.vesko.capstone.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.vesko.capstone.R;
import com.example.vesko.capstone.model.Restaurant;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class FavoriteRestaurantsWidget extends AppWidgetProvider {
    public static ArrayList<Restaurant> restaurantsArrayList=null;
    public static int list_position=0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.favorite_restaurants_widget);


        if(!restaurantsArrayList.isEmpty()) {
        Intent intent = new Intent(context, ListWidgetService.class);
        views.setRemoteAdapter(R.id.widget_list_restaurants, intent);

        Intent appIntent = new Intent();
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widget_list_restaurants, appPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

        appWidgetManager.updateAppWidget(appWidgetId, views);

    }




    @Override
    public void onReceive(Context context, Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, FavoriteRestaurantsWidget.class));
        if (intent.getAction().equals("android.appwidget.action.CUSTOM_INTENT2")) {
            restaurantsArrayList = new ArrayList<>();
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_restaurants);
            Bundle data = intent.getExtras();
            try {
                restaurantsArrayList = data.getParcelableArrayList("restaurants_array_list_receiver");
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_restaurants);
                onUpdate(context, appWidgetManager, appWidgetIds);

            } catch (Exception e) {
                Log.d("errrror", "erorrr");
            }
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId );
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

