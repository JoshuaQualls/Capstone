package com.example.tedge.barbuds;

import com.google.android.gms.maps.model.LatLng;

public class Bar {
    private int id;
    private String name;
    private LatLng location;

    public Bar(int id, String name, LatLng location){
        this.id = id;
        this.name = name;
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public LatLng getLocation(){
        return location;
    }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof Bar)){
            return false;
        }
        else if( ((Bar) obj).getId() != this.id){
            return false;
        }
        else{
            return true;
        }
    }

}
