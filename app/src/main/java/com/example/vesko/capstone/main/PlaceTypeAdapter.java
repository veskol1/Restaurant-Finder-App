package com.example.vesko.capstone.main;

import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.vesko.capstone.model.PlaceTypeModel;
import com.example.vesko.capstone.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceTypeAdapter extends RecyclerView.Adapter<PlaceTypeAdapter.PlaceTypeViewHolder> {
    final private GridImageClickListener mOnClickListener;
    int numberOfImages;
    private static int viewHolderCount;
    private DisplayMetrics displayMetrics;
    ArrayList<PlaceTypeModel> arrayPlaceTypes;
    private int orientation;

    public PlaceTypeAdapter(int numberOfImages,DisplayMetrics displayMetrics,GridImageClickListener mOnClickListener,int orientation){
        this.mOnClickListener=mOnClickListener;
        this.numberOfImages=numberOfImages;
        this.displayMetrics=displayMetrics;
        this.orientation=orientation;
        viewHolderCount=0;

        addPlaceTypeImagesToModel();

    }

    public interface GridImageClickListener {
        public void clickOnRestaurantType(int position);
    }


    @Override
    public PlaceTypeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.restaurant_type_image_view;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        PlaceTypeViewHolder viewHolder = new PlaceTypeViewHolder(view);

        viewHolderCount++;
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return numberOfImages;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceTypeViewHolder holder, int position) {
        Picasso.get()
                .load(arrayPlaceTypes.get(position).getPlaceImg())
                .into(holder.restImageView);
    }

    public class PlaceTypeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.rest_type_image) ImageView restImageView;

        public PlaceTypeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                restImageView.getLayoutParams().height = displayMetrics.widthPixels / 8 ;
                restImageView.getLayoutParams().width = displayMetrics.widthPixels / 8 ;
            }
            else{
                restImageView.getLayoutParams().height = displayMetrics.widthPixels / 5 ;
                restImageView.getLayoutParams().width = displayMetrics.widthPixels / 5 ;
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.clickOnRestaurantType(clickedPosition);
        }
    }

    public void addPlaceTypeImagesToModel(){
        arrayPlaceTypes = new ArrayList<>();
        arrayPlaceTypes.add( new PlaceTypeModel("Delivery",R.drawable.ic_delivery_plus_text));
        arrayPlaceTypes.add( new PlaceTypeModel("Dinner",R.drawable.ic_dinner_plus_text));
        arrayPlaceTypes.add( new PlaceTypeModel("Lunch",R.drawable.ic_lunch_plus_text));
        arrayPlaceTypes.add( new PlaceTypeModel("Take Away",R.drawable.ic_take_away_plus_text));
        arrayPlaceTypes.add( new PlaceTypeModel("Breakfast",R.drawable.ic_breakfast_plus_text));
        arrayPlaceTypes.add( new PlaceTypeModel("Nightlife",R.drawable.ic_nightlife_plus_text));
    }


}
