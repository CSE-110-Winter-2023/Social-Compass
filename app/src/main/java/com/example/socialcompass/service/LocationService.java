/**
 * This file has LocationService class which is used
 * to track the location
 */

package com.example.socialcompass.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialcompass.entity.Position;

/**
 * This class is LocationService class used to track users' location
 */
public class LocationService implements LocationListener, Service {
    private static LocationService instance;
    private Activity activity;


    private MutableLiveData<Position> locationValue;

    private final LocationManager locationManager;

    /**
     * Singleton for LocationService
     *
     * @param activity Context needed to initiate LocationManager
     * @return LocationService instance
     */
    public static LocationService singleton(Activity activity) {
        if (instance == null) {
            instance = new LocationService(activity);
        }

        return instance;
    }

    /**
     * Constructor for LocationService
     *
     * @param activity Context needed to initiate LocationManager
     */

    protected LocationService(Activity activity) {
        this.locationValue = new MutableLiveData<>();
        this.activity = activity;
        this.locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        //Register sensor listeners
        this.registerSensorListeners();
    }

    /**
     * Register location listener
     */
    public void registerSensorListeners() {
        if (ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new IllegalStateException("Apps needs location permission to get latest location");
        }

        this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    /**
     * Callback for location updates
     *
     * @param location New location
     */

    @Override
    public void onLocationChanged(@NonNull Location location) {
        this.locationValue.postValue(new Position(location.getLatitude(), location.getLongitude()));
    }

    /**
     * Unregister location listener
     */
    public void unregisterSensorListeners() {
        locationManager.removeUpdates(this);
    }

    /**
     * Get location
     *
     * @return Location
     */
    public LiveData<Position> getLocation() {
        return this.locationValue;
    }

    /**
     * Unregister location listener on pause
     */
    public void onPause() {
        this.unregisterSensorListeners();
    }

    public long timeSinceGpsUpdate() {
        @SuppressLint("MissingPermission") Location loc = instance.locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        return System.currentTimeMillis() - loc.getTime();

    }
}
