package com.example.vesko.capstone.main;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.vesko.capstone.R;
import com.example.vesko.capstone.data.RestaurantContract;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteRestaurantCursorAdapter extends RecyclerView.Adapter<FavoriteRestaurantCursorAdapter.FavoriteRestaurantViewHolder> {

    private Context mContext;
    private Cursor mCursor;
    final private RestaurantButtonsClickListener mOnClickListener;

    public FavoriteRestaurantCursorAdapter( Context context,RestaurantButtonsClickListener mOnClickListener) {
        this.mOnClickListener=mOnClickListener;
        this.mContext=context;
    }

    @Override
    @NonNull
    public FavoriteRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.favorite_restaurant_view, parent, false);
        return new FavoriteRestaurantViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final FavoriteRestaurantViewHolder holder, final int position) {
        mCursor.moveToPosition(position);
        String restName=mCursor.getString(mCursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_NAME));

        holder.itemView.setTag(mCursor.getString(mCursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_ID)));
        String restImage=mCursor.getString(mCursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_IMAGE));

        if(!restImage.equals("") ) {
            Picasso.get()
                    .load(restImage)
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
        holder.restNameTextView.setText(restName);
        String restRating = mCursor.getString(mCursor.getColumnIndex(RestaurantContract.RestaurantEntry.COLUMN_RESTAURANT_RATING));
        holder.restRatingBarView.setRating((float)Double.parseDouble(restRating));
    }


    public interface RestaurantButtonsClickListener {
        public void onLocationRestListener(int position);
        public void onFavRestClickListener(int position);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        // check if this cursor is the same as the previous cursor (mCursor)
        if (mCursor == c) {
            return null; // bc nothing has changed
        }
        Cursor temp = mCursor;
        this.mCursor = c; // new cursor value assigned

        //check if this is a valid cursor, then update the cursor
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }


    public class FavoriteRestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.restaurant_icon) ImageView restImageView;
        @BindView(R.id.restaurant_name) TextView restNameTextView;
        @BindView(R.id.rating_bar) RatingBar restRatingBarView;
        @BindView(R.id.restaurant_location_button) ImageButton restLocationButton;
        @BindView(R.id.restaurant_favorite_button) ImageButton restFavButton;

        public FavoriteRestaurantViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
            restLocationButton.setOnClickListener(this);
            restFavButton.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            if(v.getId()== restLocationButton.getId())
                mOnClickListener.onLocationRestListener(clickedPosition);
            else if(v.getId()==restFavButton.getId())
                mOnClickListener.onFavRestClickListener(clickedPosition);
        }
    }
}
