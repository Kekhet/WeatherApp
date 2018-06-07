package com.example.kekhet.lab12;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class City {
    public City(String name, Double lat, Double lon) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public City() {
    }

    String name;
    Double lat, lon;


    public static City[] setDefault(){
        City retVal[] = new City[6];
        retVal[0] = new City("GPS",-1.,-1.);
        retVal[1] = new City("Krakow",50.0619,19.9369);
        retVal[2] = new City("Warsaw",52.2319,21.0067);
        retVal[3] = new City("Gdansk",54.3521,18.6464);
        retVal[4] = new City("Krosno",49.6938,21.7652);
        retVal[5] = new City("Szczecin",53.4302,14.551);
        return retVal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }
}

