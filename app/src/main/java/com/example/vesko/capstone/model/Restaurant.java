package com.example.vesko.capstone.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Restaurant implements Parcelable{
    private String restId;
    private String restName;
    private String restImage;
    private String restRating;
    private String restPriceRange;
    private Location location;


    public static final Parcelable.Creator CREATOR =new Parcelable.Creator() {
        @Override
        public Restaurant createFromParcel(Parcel source) {
            return new Restaurant(source);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };


    public Restaurant(Parcel in){
        this.location=(Location)in.readParcelable(Location.class.getClassLoader());
        this.restId=in.readString();
        this.restName=in.readString();
        this.restImage=in.readString();
        this.restRating=in.readString();
        this.restPriceRange=in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location,flags);
        dest.writeString(restId);
        dest.writeString(restName);
        dest.writeString(restImage);
        dest.writeString(restRating);
        dest.writeString(restPriceRange);


    }


    public Restaurant(String restId,String restName,String restImage, String restRating, String restPriceRange, Location location ){
        this.restId=restId;
        this.restName=restName;
        this.restImage=restImage;
        this.restRating=restRating;
        this.restPriceRange=restPriceRange;
        this.location=location;
    }

    public String getRestId() {
        return restId;
    }

    public Location getLocation() {
        return location;
    }

    public String getRestImage() {
        return restImage;
    }

    public String getRestName() {
        return restName;
    }


    public String getRestPriceRange() {
        return restPriceRange;
    }

    public String getRestRating() {
        return restRating;
    }


    public void setLocation(Location location) {
        this.location = location;
    }

    public void setRestImage(String restImage) {
        this.restImage = restImage;
    }

    public void setRestName(String restName) {
        this.restName = restName;
    }


    public void setRestPriceRange(String restPriceRange) {
        this.restPriceRange = restPriceRange;
    }

    public void setRestRating(String restRating) {
        this.restRating = restRating;
    }

}
