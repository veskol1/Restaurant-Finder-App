package com.example.vesko.capstone.model;

public class PlaceTypeModel {
    private String placeName;
    private int placeImg;

    public PlaceTypeModel(String placeName, int placeImg){
        this.placeName=placeName;
        this.placeImg = placeImg;
    }

    public int getPlaceImg() {
        return placeImg;
    }
}
