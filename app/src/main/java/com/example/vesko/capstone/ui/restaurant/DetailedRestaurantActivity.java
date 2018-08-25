package com.example.vesko.capstone.ui.restaurant;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vesko.capstone.R;
import com.example.vesko.capstone.main.MainActivity;
import com.example.vesko.capstone.model.Restaurant;
import com.example.vesko.capstone.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailedRestaurantActivity extends AppCompatActivity implements RestaurantAdapter.LinearRestaurantClickListener {

    private String RADIUS="200"; //200 meter
    private String COUNT_OF_RESAULTS="20";  //by Default
    private String SORT_TYPE="rating";
    private String restType ="";
    private String restTypeString="";
    private String entityId ="";
    private ArrayList<Restaurant> arrayRestaurants;
    private RecyclerView mRestaurantsList;
    private RestaurantAdapter mAdapter;
    private double lat=0;
    private double lon=0;
    private URL searchLocationUrl;
    public static int NUMBER_OF_RESTAURANT_TYPES=6;
    private ArrayList<String> favRestIds;

    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.restaurant_location_view) TextView mRestaurantLocationView;
    @BindView(R.id.restaurant_type_view) TextView mRestaurantTypeView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_restaurant);
        setTitle("Restaurant Finder - Rating");

        ButterKnife.bind(this);
        arrayRestaurants= new ArrayList<>();
        favRestIds = new ArrayList<>();

        Bundle data = getIntent().getExtras();
        if(savedInstanceState==null)
            favRestIds = data.getStringArrayList("favorite_restaurant_ids_array_list");
        else
            favRestIds = savedInstanceState.getStringArrayList("store_new_favorite_restaurants_array_list");

        mRestaurantLocationView.setText(data.getString("address"));
        restType = findRestaurantCategory(data.getInt("category"));
        mRestaurantTypeView.setText(restTypeString);

        if(MainActivity.locationByLatLon){ //fetch data by maps location
             lat= data.getDouble("latitude");
             lon= data.getDouble("longitude");

            searchLocationUrl = NetworkUtils.checkLocation(String.valueOf(lat),String.valueOf(lon)); /*default query search*/
            new LocationQueryTask().execute(searchLocationUrl);
        }
        else {//fetch data from entered address
            searchLocationUrl = NetworkUtils.buildAddressUrl(data.getString("address"));
            new AddressQueryTask().execute(searchLocationUrl);
        }
        mRestaurantsList = (RecyclerView)findViewById(R.id.restaurants_search_result_recycler_view);
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        mRestaurantsList.setLayoutManager(linearManager);
        mRestaurantsList.setHasFixedSize(true);



    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("store_new_favorite_restaurants_array_list", favRestIds);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemThatWasClickedId = item.getItemId();

        if (itemThatWasClickedId == R.id.action_menu_rating) {
            setTitle("Restaurant Finder - by Rating");
            arrayRestaurants.clear();
            SORT_TYPE="rating";
            if(MainActivity.locationByLatLon)
                new LocationQueryTask().execute(searchLocationUrl);
             else
                new AddressQueryTask().execute(searchLocationUrl);
            return true;
        }
        else if(itemThatWasClickedId==R.id.action_menu_cost){
            setTitle("Restaurant Finder - Cost");
            arrayRestaurants.clear();
            SORT_TYPE="cost";
            if(MainActivity.locationByLatLon)
                new LocationQueryTask().execute(searchLocationUrl);
            else
                new AddressQueryTask().execute(searchLocationUrl);
            return true;
        }
        else if(itemThatWasClickedId==R.id.action_menu_distance){
            setTitle("Restaurant Finder - Distance");
            arrayRestaurants.clear();
            SORT_TYPE="distance";
            if(MainActivity.locationByLatLon)
                new LocationQueryTask().execute(searchLocationUrl);
            else
                new AddressQueryTask().execute(searchLocationUrl);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    public class LocationQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String locationSearchJsonResults = null;
            try {
                locationSearchJsonResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return locationSearchJsonResults;
        }

        @Override
        protected void onPostExecute(String locationSearchJsonResults) {
            if (locationSearchJsonResults != null){
                URL searchUrl = NetworkUtils.buildUrlFromLocation(String.valueOf(lat), String.valueOf(lon), RADIUS, COUNT_OF_RESAULTS, restType, SORT_TYPE); /*default query search*/
                new RestaurantQueryTask().execute(searchUrl);
            }
            else{
                Toast.makeText(getApplicationContext(), "Couldn't find restaurants. Try different address Or check Internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    /*After we add/remove(executed in adapter) restaurant from favorite list (by clicking heart button)
     this function will update the number of favorite restaurants ids in array*/
    @Override
    public void onFavRestClickListener(ArrayList<String>favRestIds) {
        this.favRestIds =favRestIds;

    }

    public void sendBroadcast(){
        Intent intent = new Intent();
        intent.putExtra("restaurants_array_list_receiver",arrayRestaurants);
        Log.d("array size is",""+arrayRestaurants.size());
        intent.setAction("android.appwidget.action.CUSTOM_INTENT2");
        sendBroadcast(intent);
    }

    public class AddressQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String locationSearchJsonResults = null;
            try {
                locationSearchJsonResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return locationSearchJsonResults;
        }

        @Override
        protected void onPostExecute(String locationSearchJsonResults) {
            if(locationSearchJsonResults==null)
                Toast.makeText(DetailedRestaurantActivity.this, "Couldn't find restaurants. Try different address Or check Internet connection", Toast.LENGTH_LONG).show();
            else {
                entityId = NetworkUtils.parseLocationJson(locationSearchJsonResults);
                if (entityId.equals("")) //empty
                    Toast.makeText(getApplicationContext(), "Couldn't find restaurants. Try valid address", Toast.LENGTH_LONG).show();
                else {
                    URL searchUrl = NetworkUtils.buildUrlFromAddress(entityId, COUNT_OF_RESAULTS, restType, SORT_TYPE); /*default query search*/
                    new RestaurantQueryTask().execute(searchUrl);   /*first time asynctask*/
                }
            }
        }
    }



    public class RestaurantQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String restaurantsSearchJsonResults = null;
            try {
                restaurantsSearchJsonResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return restaurantsSearchJsonResults;
        }

        @Override
        protected void onPostExecute(String restaurantsSearchJsonResults) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            arrayRestaurants = NetworkUtils.parseSearchJson(restaurantsSearchJsonResults);
            if((arrayRestaurants.isEmpty()))
                Toast.makeText(getApplicationContext(), "Couldn't find restaurants, Please try different address", Toast.LENGTH_LONG).show();
            else {
                mAdapter = new RestaurantAdapter(arrayRestaurants.size(), arrayRestaurants, DetailedRestaurantActivity.this,DetailedRestaurantActivity.this, favRestIds);
                mRestaurantsList.setAdapter(mAdapter);
            }
          }
    }

    @Override
    public void onLocationRestListener(int position) {
        String lat = arrayRestaurants.get(position).getLocation().getLat();
        String lon = arrayRestaurants.get(position).getLocation().getLon();

        String addressUri="geo:";
        addressUri=addressUri+lat+","+lon+"?q="+arrayRestaurants.get(position).getRestName();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(addressUri));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    /*All the Category id's are taken from Zomato API*/
    public String findRestaurantCategory(int clickPositionImage){
        String restCategory="";
        switch (clickPositionImage) {
            case 0: {
                restCategory = "1"; //delivery
                restTypeString="Delivery";
                break;
            }
            case 1:  {
                restCategory = "10"; //dinner
                restTypeString="Dinner";
                break;
            }
            case 2:  {
                restCategory = "9"; //lunch}
                restTypeString="Lunch";
                break;
            }
            case 3:  {
                restCategory = "5"; //take away
                restTypeString="Take Away";
                break;
            }
            case 4:  {
                restCategory = "8"; //breakfast
                restTypeString="Breakfast";
                break;
            }
            case 5: {
                restCategory = "3"; //nightlife
                restTypeString="Nightlife";
            }
        }
        return restCategory;
    }

}

