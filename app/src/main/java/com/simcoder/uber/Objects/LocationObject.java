package com.simcoder.uber.Objects;

import com.google.android.gms.maps.model.LatLng;

/**
 * Location Object used to know pickup and destination location
 */
public class LocationObject {

    private LatLng coordinates;
    private String name = "";

    /**
     * LocationObject constructor
     * @param coordinates - latLng of the location
     * @param name - name of the location
     */
    public LocationObject(LatLng coordinates, String name){
        this.coordinates = coordinates;
        this.name = name;
    }

    /**
     * LocationObject constructor
     * Creates an empty object
     */
    public LocationObject(){
    }


    public LatLng getCoordinates() {
        return new LatLng(0,0);
    }
    public void setCoordinates(LatLng coordinates) {
        this.coordinates = coordinates;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public static LocationObject getUserLocation(){
        LocationObject location = new LocationObject();
        location.setCoordinates(new LatLng(29.976480
                ,31.131302));
        location.setName("Giza pyramids");
        return location;
    }

    public static LocationObject getDriverStartLocation(){
        LocationObject location = new LocationObject();
        location.setCoordinates(new LatLng(29.990755
                ,31.151274999999998));
        location.setName("Haram Hospital");
        return location;
    }
    public static LocationObject getDestination(){
        LocationObject location = new LocationObject();
        location.setCoordinates(new LatLng(30.016894,31.377034));
        location.setName("Nacer city");
        return location;
    }



}
