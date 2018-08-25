package com.example.vesko.capstone.main;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.vesko.capstone.ui.restaurant.DetailedRestaurantActivity;
import com.example.vesko.capstone.R;
import com.example.vesko.capstone.data.RestaurantContract;
import com.example.vesko.capstone.model.Location;
import com.example.vesko.capstone.model.Restaurant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements PlaceTypeAdapter.GridImageClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
                                                                LoaderManager.LoaderCallbacks<Cursor>, FavoriteRestaurantCursorAdapter.RestaurantButtonsClickListener {

    private PlaceTypeAdapter mPlaceAdapter;
    private FavoriteRestaurantCursorAdapter mFavRestCursorAdapter;
    private GoogleApiClient mClient;
    private double lat=0;
    private double lon=0;
    public static int scrollX = 0;
    public static int scrollY = -1;
    public static boolean locationByLatLon;

    public static final String EXTRA_PLANT_ID = "com.example.android.MainActivity.extra.PLANT_ID";

    public static final int NUMBER_OF_RESTAURANT_TYPES=6;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int TASK_LOADER_ID = 0;
    public static final String TAG = MainActivity.class.getSimpleName();

    private ArrayList<String> favRestIds;
    private ArrayList<Restaurant> arrayRestaurants;

    @BindView(R.id.scroll_view) ScrollView scroll;
    @BindView(R.id.edit_text_address) EditText addressInputText;
    @BindView(R.id.restaurant_type_recycler_view) RecyclerView mPlaceTypeGrid;
    @BindView(R.id.favorite_restaurant_recycler_view) RecyclerView mFavoriteRestaurantsList;
    @BindView(R.id.broadcastButton) Button mButtonBroadcast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationByLatLon=false;
        ButterKnife.bind(this);
        GridLayoutManager gridManager;
        int orientation = this.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridManager = new GridLayoutManager(this,6);
        }
        else
             gridManager = new GridLayoutManager(this,3);
        mPlaceTypeGrid.setLayoutManager(gridManager);
        mPlaceTypeGrid.setHasFixedSize(true);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mPlaceAdapter = new PlaceTypeAdapter(NUMBER_OF_RESTAURANT_TYPES,displayMetrics,MainActivity.this,orientation);
        mPlaceTypeGrid.setAdapter(mPlaceAdapter);


        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        mFavoriteRestaurantsList.setLayoutManager(linearManager);
        mFavoriteRestaurantsList.setHasFixedSize(true);
        arrayRestaurants = new ArrayList<>();

        getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        mFavRestCursorAdapter = new FavoriteRestaurantCursorAdapter(MainActivity.this,MainActivity.this);
        mFavoriteRestaurantsList.setAdapter(mFavRestCursorAdapter);

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();


        mButtonBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("restaurants_array_list_receiver",arrayRestaurants);
                Log.d("array size is",""+arrayRestaurants.size());
                intent.setAction("android.appwidget.action.CUSTOM_INTENT2");
                sendBroadcast(intent);
            }
        });

        /*handles swipe and delete favorite restaurant*/
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                String restId = (String) viewHolder.itemView.getTag();
                Uri uri = RestaurantContract.RestaurantEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(restId).build();
                getContentResolver().delete(uri, null, null);
                getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);
            }
        }).attachToRecyclerView(mFavoriteRestaurantsList);

        if(savedInstanceState!=null)
            mFavoriteRestaurantsList.scrollToPosition(savedInstanceState.getInt("recycler_view_position"));
    }
    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*save Restaurants recyclerView list position on rotation screen*/
        outState.putInt("recycler_view_position", mFavoriteRestaurantsList.getScrollState());
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor cursor = null;
            @Override
            protected void onStartLoading() {
                if (cursor != null) {
                    deliverResult(cursor); // Delivers any previously loaded data immediately
                } else {
                    forceLoad();   // Force a new load
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(RestaurantContract.RestaurantEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            null);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            public void deliverResult(Cursor data) {
                arrayRestaurants.clear();
                cursor = data;
                getAllFavoriteRestaurantsIds(data);
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the data that the adapter uses to create ViewHolders
        mFavRestCursorAdapter.swapCursor(data);
        updateScrollViewPosition();
    }

    public void getAllFavoriteRestaurantsIds(Cursor cursor){
        favRestIds = new ArrayList<>();


        if (cursor!=null) {

            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                String restId = cursor.getString(cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_ID));
                String restName = cursor.getString(cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_NAME));
                String restImage = cursor.getString(cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_IMAGE));
                String restRating = cursor.getString(cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_RATING));
                String restPriceRange = cursor.getString(cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_PRICE));

                String locationCityName = cursor.getString(cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_LOCATION_CITY_NAME));
                String locationAddress = cursor.getString(cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_LOCATION_ADDRESS));
                String locationLat = cursor.getString(cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_LOCATION_LATITUDE));
                String locationLon = cursor.getString(cursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_LOCATION_LONGITUDE));

                Log.d("locationLat=",locationLat);
                Log.d("locationLon=",locationLon);
                Location location = new Location(locationCityName, locationAddress, locationLat, locationLon);
                Restaurant rest = new Restaurant(restId, restName, restImage, restRating, restPriceRange, location);

                arrayRestaurants.add(rest);
                favRestIds.add(restId);
            }

        }
    }

    public void updateScrollViewPosition(){
        scroll.post(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        scroll.scrollTo(scrollX, scrollY);
                    }
                });
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mFavRestCursorAdapter.swapCursor(null);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) { Log.i(TAG, "API Client Connection Successful!"); }

    @Override
    public void onConnectionSuspended(int i) { }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) { }

    public void onLocationButtonClicked(View view){
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSIONS_REQUEST_FINE_LOCATION);
        try {

            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent i = builder.build(this);
            startActivityForResult(i, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, String.format("GooglePlayServices Not Available [%s]", e.getMessage()));
        } catch (Exception e) {
            Log.e(TAG, String.format("PlacePicker Exception: %s", e.getMessage()));
        }
    }

    /*get Address by google maps location callback*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            if (place == null) {
                Log.i(TAG, "No place selected");
                return;
            }
            // Extract the place information from the API
            String placeAddress = place.getAddress().toString();
            LatLng location = place.getLatLng();
            lat=location.latitude;
            lon=location.longitude;
            addressInputText.setText(placeAddress);

            locationByLatLon=true;
        }
    }


    @Override
    public void clickOnRestaurantType(int position) {
        ImageView restTypeImage = (ImageView)findViewById(R.id.rest_type_image);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,restTypeImage , "imageType");
        Intent intent = new Intent(this,DetailedRestaurantActivity.class);
        if(addressInputText.getText().toString().isEmpty()){
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.coordinator), "Please Enter Valid Address", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        else {
            if(locationByLatLon) {
                intent.putExtra("latitude", lat);
                intent.putExtra("longitude", lon);
            }
            intent.putExtra("category", position);
            intent.putStringArrayListExtra("favorite_restaurant_ids_array_list", favRestIds);
            intent.putExtra("address", addressInputText.getText().toString());
            startActivity(intent, options.toBundle());

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
        startActivity(intent);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivity(intent);
//        }
    }

    @Override
    public void onFavRestClickListener(int position) {
        Toast.makeText(getApplicationContext(), "Swipe left/right to remove", Toast.LENGTH_LONG).show();

//        Log.d("delete","delete22");
//        String restId = arrayRestaurants.get(position).getRestId();
//        Uri uri = RestaurantContract.RestaurantEntry.CONTENT_URI;
//        uri = uri.buildUpon().appendPath(restId).build();
//        getContentResolver().delete(uri, null, null);
//        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, MainActivity.this);
    }
}


