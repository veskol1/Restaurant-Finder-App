package com.example.vesko.capstone.widget;


import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.vesko.capstone.R;
import com.example.vesko.capstone.data.RestaurantContract;
import com.example.vesko.capstone.model.Restaurant;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import java.io.IOException;
import java.util.ArrayList;

import static com.example.vesko.capstone.data.RestaurantContract.BASE_CONTENT_URI;
import static com.example.vesko.capstone.data.RestaurantContract.PATH_TASKS;
import static com.example.vesko.capstone.widget.FavoriteRestaurantsWidget.restaurantsArrayList;
import static com.example.vesko.capstone.widget.FavoriteRestaurantsWidget.list_position;

public class ListWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(),restaurantsArrayList);
    }
}


class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
     Context mContext;
    Cursor mCursor;
     ArrayList<Restaurant> restaurants;
     int numberOfItems=0;


    public ListRemoteViewsFactory(Context applicationContext, ArrayList<Restaurant> restaurantsArrayList){
        mContext=applicationContext;
        this.restaurants =restaurantsArrayList;
        this.numberOfItems = this.restaurants.size();
    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
            this.restaurants =restaurantsArrayList;
            this.numberOfItems=restaurants.size();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return numberOfItems;
    }

    @Override
    public RemoteViews getViewAt(final int position) {
        final RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_view);

        views.setTextViewText(R.id.restaurant_name_widget,restaurants.get(position).getRestName());
        String restImage = restaurants.get(position).getRestImage();

        String lat = restaurantsArrayList.get(position).getLocation().getLat();
        String lon = restaurantsArrayList.get(position).getLocation().getLon();

        String addressUri="geo:";
        addressUri=addressUri+lat+","+lon+"?q="+restaurantsArrayList.get(position).getRestName();

        Intent fillInIntent = new Intent(Intent.ACTION_VIEW);
        fillInIntent.setData(Uri.parse(addressUri));


        views.setOnClickFillInIntent(R.id.restaurant_location_button_widget, fillInIntent);



//        Picasso.get()
//                .load(restaurants.get(position).getRestImage())
//                .placeholder(R.drawable.restaurant_icon)
//                .error(R.drawable.restaurant_icon)
//                .centerCrop()
//                .fit()
//                .into(views,R.id.restaurant_icon_widget,new int[]appWidgetId);

//        Handler uiHandler = new Handler(Looper.getMainLooper());
//        uiHandler.post(new Runnable(){
//            @Override
//            public void run() {
//                Picasso.get()
//                        .load(restaurants.get(position).getRestImage())
//                        .placeholder(R.drawable.restaurant_icon)
//                        .into(new Target() {
//                            @Override
//                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//                                Log.d("not loaded1","not loaded1");
//                            }
//
//                            @Override
//                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                views.setImageViewBitmap(R.id.restaurant_icon_widget,bitmap);
//                                Log.d("not loaded1","not loaded2");
//                                Log.d("rest image",""+restaurants.get(position).getRestImage());
//                            }
//
//
//                            @Override
//                            public void onPrepareLoad(Drawable placeHolderDrawable) {
//                                //do something while loading                                }
//                            }});
//            }
//        });





        return views;
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
