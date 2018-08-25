package com.example.vesko.capstone.ui.restaurant;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vesko.capstone.R;
import com.example.vesko.capstone.data.RestaurantContract;
import com.example.vesko.capstone.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private ArrayList<Restaurant> arrayRestaurants = new ArrayList<>();
    int numberOfRestaurants;
    private static int viewHolderCount;
    final private Context context;
    final private LinearRestaurantClickListener mOnClickListener;
    private ArrayList<String>favRestIds = new ArrayList<>();


    public RestaurantAdapter(int numberOfRestaurants, ArrayList<Restaurant> arrayRestaurants,LinearRestaurantClickListener mOnClickListener,Context context,ArrayList<String>favRestIds) {
        this.arrayRestaurants =arrayRestaurants;
        this.numberOfRestaurants=numberOfRestaurants;
        this.mOnClickListener=mOnClickListener;
        this.context=context;
        this.favRestIds=favRestIds;
        viewHolderCount=0;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.search_restaurant_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        RestaurantViewHolder viewHolder = new RestaurantViewHolder(view);

        viewHolderCount++;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RestaurantViewHolder holder, final int position) {
            //Log.d("image1",""+arrayRestaurants.get(position).getRestImage());
        if(  !arrayRestaurants.get(position).getRestImage().isEmpty() && arrayRestaurants.get(position).getRestImage()!=null ) {
            Picasso.get()
                    .load(arrayRestaurants.get(position).getRestImage())
                    .placeholder(R.drawable.restaurant_icon)
                    .error(R.drawable.restaurant_icon)
                    .centerCrop()
                    .fit()
                    .into(holder.restImageView);
        }
        else {
            Picasso.get()
                    .load(R.drawable.restaurant_icon)
                    .error(R.drawable.restaurant_icon)
                    .into(holder.restImageView);
        }
        holder.restNameTextView.setText(arrayRestaurants.get(position).getRestName());
        holder.restRatingBarView.setRating((float)Double.parseDouble(arrayRestaurants.get(position).getRestRating()));
        String restPrice=arrayRestaurants.get(position).getRestPriceRange();
        switch (restPrice) {
            case "1": {
                holder.restPriceImageView.setImageResource(R.drawable.price1);
                break;
            }
            case "2": {
                holder.restPriceImageView.setImageResource(R.drawable.price2);
                break;
            }
            case "3": {
                holder.restPriceImageView.setImageResource(R.drawable.price3);
                break;
            }
            case "4": {
                holder.restPriceImageView.setImageResource(R.drawable.price4);
                break;
            }
        }
        //check if restaurant is already in favorite restaurant list
        Boolean checkIfAlreadyFavoriteRestaurant=false;
        for(int i=0; i<favRestIds.size(); i++){
            if(arrayRestaurants.get(position).getRestId().equals(favRestIds.get(i))) {
                checkIfAlreadyFavoriteRestaurant = true;
                break;
            }
        }//updates his heart icon
        if(checkIfAlreadyFavoriteRestaurant)
            holder.restFavButton.setImageResource(R.drawable.ic_restaurant_favorite);

        /*add/remove restaurant from db*/
        holder.restFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = holder.restFavButton.getDrawable();
                if (drawable.getConstantState().equals(context.getResources().getDrawable(R.drawable.ic_restaurant_unfavorite).getConstantState())){
                    holder.restFavButton.setImageResource(R.drawable.ic_restaurant_favorite);
                    addToDb(position);
                    mOnClickListener.onFavRestClickListener(favRestIds);
                }
                else{
                    holder.restFavButton.setImageResource(R.drawable.ic_restaurant_unfavorite);
                    removeFromDb(position);
                    mOnClickListener.onFavRestClickListener(favRestIds);
                }
            }
        });

    }

    public void addToDb(int position){
        ContentValues cv = new ContentValues();
        cv.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_ID, arrayRestaurants.get(position).getRestId());
        cv.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_NAME, arrayRestaurants.get(position).getRestName());
        cv.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_IMAGE, arrayRestaurants.get(position).getRestImage());
        cv.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_RATING, arrayRestaurants.get(position).getRestRating());
        cv.put(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_PRICE, arrayRestaurants.get(position).getRestPriceRange());

        cv.put(RestaurantContract.RestaurantEntry.COLUMN_LOCATION_CITY_NAME, arrayRestaurants.get(position).getLocation().getCityName());
        cv.put(RestaurantContract.RestaurantEntry.COLUMN_LOCATION_ADDRESS, arrayRestaurants.get(position).getLocation().getAddress());
        cv.put(RestaurantContract.RestaurantEntry.COLUMN_LOCATION_LATITUDE, arrayRestaurants.get(position).getLocation().getLat());
        cv.put(RestaurantContract.RestaurantEntry.COLUMN_LOCATION_LONGITUDE, arrayRestaurants.get(position).getLocation().getLon());
        favRestIds.add(arrayRestaurants.get(position).getRestId());
        Uri uri = context.getContentResolver().insert(RestaurantContract.RestaurantEntry.CONTENT_URI, cv);
        if(uri != null) {
            Toast.makeText(context, "Restaurant added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeFromDb(int position){
        String restId = arrayRestaurants.get(position).getRestId();
        Uri uri = RestaurantContract.RestaurantEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(restId).build();

        if(uri != null) {
            Toast.makeText(context, "Restaurant removed from favorites", Toast.LENGTH_SHORT).show();
        }
        // COMPLETED (2) Delete a single row of data using a ContentResolver
        context.getContentResolver().delete(uri, null, null);

        for(int i=0;i<favRestIds.size(); i++)
            if(favRestIds.get(i).equals(restId)) {
                favRestIds.remove(i);
                break;
            }
    }


    public interface LinearRestaurantClickListener{
        public void onLocationRestListener(int position);
        public void onFavRestClickListener(ArrayList<String>favRestIds);
    }

    @Override
    public int getItemCount() {
        return numberOfRestaurants;
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.restaurant_icon) ImageView restImageView;
        @BindView(R.id.price_image) ImageView restPriceImageView;
        @BindView(R.id.restaurant_name) TextView restNameTextView;
        @BindView(R.id.rating_bar) RatingBar restRatingBarView;
        @BindView(R.id.restaurant_location_button) ImageButton restLocationButton;
        @BindView(R.id.restaurant_favorite_button) ImageButton restFavButton;


        public RestaurantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            restLocationButton.setOnClickListener(this);
            restFavButton.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            if(v.getId()== restLocationButton.getId())
                mOnClickListener.onLocationRestListener(clickedPosition);
            if(v.getId()== restFavButton.getId())
                mOnClickListener.onFavRestClickListener(favRestIds);
        }
    }
}
