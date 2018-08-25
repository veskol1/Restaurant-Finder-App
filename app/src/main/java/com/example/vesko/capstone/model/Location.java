package com.example.vesko.capstone.model;


import android.os.Parcel;
import android.os.Parcelable;

public class Location implements Parcelable{

    private String cityName;
    private String address;
    private String lat;
    private String lon;


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public Location(Parcel in){
        this.cityName=in.readString();
        this.address=in.readString();
        this.lat=in.readString();
        this.lon=in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cityName);
        dest.writeString(address);
        dest.writeString(lat);
        dest.writeString(lon);
    }

    public Location(String cityName, String address, String lat, String lon){
        this.cityName=cityName;
        this.address=address;
        this.lat=lat;
        this.lon=lon;
    }

    public String getAddress() {
        return address;
    }

    public String getCityName() {
        return cityName;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
