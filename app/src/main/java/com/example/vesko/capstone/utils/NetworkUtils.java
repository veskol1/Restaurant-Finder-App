package com.example.vesko.capstone.utils;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.vesko.capstone.model.Location;
import com.example.vesko.capstone.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class NetworkUtils {

    private final static String ZOMATO_BASE_URL = "https://developers.zomato.com/api/v2.1/search?";
    private final static String LOCATION_BASE_URL = "https://developers.zomato.com/api/v2.1/locations?";
    private final static String GEO_BASE_URL = "https://developers.zomato.com/api/v2.1/geocode?";
    private final static String ENTITY_ID = "entity_id";
    private final static String ENTITY_TYPE="entity_type";
    private final static String COUNT = "count";
    private final static String CATEGORY = "category";
    private final static String SORT = "sort";

    final static String KEY="2ca80e66f8f7210323132d35e2abfe80";

    public static URL buildAddressUrl(String address) {
        Uri builtUri = Uri.parse(LOCATION_BASE_URL).buildUpon()
                .appendQueryParameter("query", address)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    public static URL checkLocation(String lan, String lon) {
        Uri builtUri = Uri.parse(GEO_BASE_URL).buildUpon()
                .appendQueryParameter("lat", lan)
                .appendQueryParameter("lon", lon)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d("url is!!!!!!!!!!!!!!!!:",""+url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    public static URL buildUrlFromLocation(String lan, String lon,String radius,String count, String category , String sort) {
        Uri builtUri = Uri.parse(ZOMATO_BASE_URL).buildUpon()
                .appendQueryParameter(COUNT, count)
                .appendQueryParameter("lat", lan)
                .appendQueryParameter("lon", lon)
                .appendQueryParameter("radius", radius)
                .appendQueryParameter(CATEGORY, category)
                .appendQueryParameter(SORT, sort)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d("url is!!!!!!!!!!!!!!!!:",""+url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static URL buildUrlFromAddress(String entityId,String count, String category , String sort) {
        Uri builtUri = Uri.parse(ZOMATO_BASE_URL).buildUpon()
                .appendQueryParameter(ENTITY_ID, entityId)
                .appendQueryParameter(ENTITY_TYPE, "city")
                .appendQueryParameter(COUNT, count)
                .appendQueryParameter(CATEGORY, category)
                .appendQueryParameter(SORT, sort)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
            Log.d("url is!!!!!!!!!!!!!!!!:",""+url.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }


    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Accept", " application/json");
        urlConnection.setRequestProperty("user-key", KEY);
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }
        finally {
            urlConnection.disconnect();
        }
    }


    public static String parseLocationJson(String json){
        String locationId="";

        try {
            JSONObject JsonObject = new JSONObject(json);
            JSONArray locationObjectsArray = JsonObject.getJSONArray("location_suggestions");
            JSONObject jsonObject = locationObjectsArray.getJSONObject(0);
            locationId=jsonObject.optString("entity_id");

        }catch (JSONException e) {
            Log.d("error","Error!");
        }
        return locationId;
    }



    public static ArrayList<Restaurant> parseSearchJson(String json){
        ArrayList<Restaurant> arrayRestaurants= new ArrayList<>();

         String restId;
         String restName;
         String restImage;
         String restRating;
         String restPriceRange;
         Restaurant restaurant;

         String cityName;
         String address;
         String lat;
         String lon;
         Location location;

        try {
            JSONObject JsonObject = new JSONObject(json);
            JSONArray restaurantsObjectsArray = JsonObject.getJSONArray("restaurants");
            for(int i=0; i<restaurantsObjectsArray.length(); i++){
                JSONObject restaurantJsonObj  = restaurantsObjectsArray.getJSONObject(i);
                JSONObject restaurantObj = restaurantJsonObj.getJSONObject("restaurant");

                restId = restaurantObj.optString("id");
                restName = restaurantObj.optString("name");
                restImage = restaurantObj.optString("featured_image");
                restPriceRange = restaurantObj.optString("price_range");

                JSONObject ratingObj = restaurantObj.getJSONObject("user_rating");
                restRating = ratingObj.optString("aggregate_rating");

                JSONObject locationObj = restaurantObj.getJSONObject("location");
                cityName=locationObj.optString("city");
                address=locationObj.optString("address");
                lat=locationObj.optString("latitude");
                lon=locationObj.optString("longitude");

                location = new Location(cityName,address,lat,lon);
                restaurant = new Restaurant(restId,restName,restImage,restRating,restPriceRange,location );

                arrayRestaurants.add(restaurant);
            }



        }catch (JSONException e) {
            Log.d("error1","Error1!");
        }
        return arrayRestaurants;
    }


}