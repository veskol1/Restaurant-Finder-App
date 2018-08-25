package com.example.vesko.capstone.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.vesko.capstone.data.RestaurantContract.*;
import com.example.vesko.capstone.model.Restaurant;

public class RestaurantDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "restaurant.db";
    private static final int DATABASE_VERSION = 11;

    public RestaurantDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_RESTAURANT_TABLE = "CREATE TABLE " + RestaurantEntry.TABLE_NAME + " (" +
//                RestaurantEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                RestaurantEntry.COLUMN_RESTAURANT_ID + " TEXT PRIMARY KEY NOT NULL,"+
                RestaurantEntry.COLUMN_RESTAURANT_NAME + " TEXT ,"+
                RestaurantEntry.COLUMN_FAVORITE + " INTEGER,"+
                RestaurantEntry.COLUMN_RESTAURANT_IMAGE + " TEXT,"+
                RestaurantEntry.COLUMN_RESTAURANT_PRICE + " TEXT,"+
                RestaurantEntry.COLUMN_RESTAURANT_RATING + " TEXT,"+
                RestaurantEntry.COLUMN_LOCATION_CITY_NAME + " TEXT,"+
                RestaurantEntry.COLUMN_LOCATION_ADDRESS + " TEXT,"+
                RestaurantEntry.COLUMN_LOCATION_LATITUDE + " TEXT,"+
                RestaurantEntry.COLUMN_LOCATION_LONGITUDE + " TEXT "+
                ");       ";

        db.execSQL(SQL_CREATE_RESTAURANT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + RestaurantEntry.TABLE_NAME);
        onCreate(db);
    }
}
