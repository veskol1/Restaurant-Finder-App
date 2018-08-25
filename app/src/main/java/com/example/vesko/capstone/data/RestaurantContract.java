package com.example.vesko.capstone.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class RestaurantContract {

    public static final String AUTHORITY = "com.example.vesko.capstone";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_TASKS = "restaurantlist";

    public static final class RestaurantEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();

        public static final String TABLE_NAME = "restaurantlist";

        public static final String COLUMN_RESTAURANT_ID = "restaurant_id";
        public static final String COLUMN_RESTAURANT_NAME = "restaurant_name";
        public static final String COLUMN_FAVORITE = "restaurant_favorite";
        public static final String COLUMN_RESTAURANT_IMAGE = "restaurant_image";
        public static final String COLUMN_RESTAURANT_RATING = "restaurant_rating";
        public static final String COLUMN_RESTAURANT_PRICE = "restaurant_price";

        public static final String COLUMN_LOCATION_CITY_NAME = "location_city_name";
        public static final String COLUMN_LOCATION_ADDRESS = "location_address";
        public static final String COLUMN_LOCATION_LATITUDE = "location_latitude";
        public static final String COLUMN_LOCATION_LONGITUDE = "location_longitude";


    }
}
