package com.example.softwareproject;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

/**
 * Reuben Sarkar
 *
 * Location service that we will use to receive location updates.
 */
public class LocationService extends Service {
    /**
     * LocationManager object that provides access to the system location services.
     */
    private LocationManager mLocationManager = null;
    /**
     * GPSListener that will listen for location updates.
     */
    private GPSListener mLocationListener = null;

    /**
     * Constructor for the service.
     */
    public LocationService() {
        super();
    }

    /**
     * Method that runs when the service is started.
     * @param intent
     *          Intent passed into the service.
     * @param flags
     *          Flag we are receiving.
     * @param startId
     *          Unique integer token to represent start request.
     * @return
     *          Flag that tells the system to create a new copy of the service when the memory is available if memory is not available.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AsyncTask2 async = new AsyncTask2();
        async.execute();
        return START_STICKY;
    }

    /**
     * AsyncTask that will keep running to broadcast the location updates to the broadcast receiver.
     */
    private class MapAsyncTask extends AsyncTask<Void, Void, Void> {
        private Location location;

        private MapAsyncTask(final Location location) {
            super();
            this.location = location;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Intent i = new Intent("locationUpdate");
            i.putExtra("lng", location.getLongitude());
            i.putExtra("lat", location.getLatitude());
            i.putExtra("user_check", "user_check");
            sendBroadcast(i);
            return null;
        }
    }

    /**
     * AsyncTask that will keep running to receive location updates from the phone's GPS.
     */
    private class AsyncTask2 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            mLocationListener = new GPSListener();
            //If we cannot get permissions then we do not request the location updates.
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, mLocationListener, Looper.getMainLooper());
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1, mLocationListener, Looper.getMainLooper());
            return null;
        }
    }

    /**
     * The LocationListener we will use to detect a location change and run the async tasks.
     */
    private class GPSListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            MapAsyncTask mat = new MapAsyncTask(location);
            mat.execute();
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {
            Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }

    /**
     * If the app is closed then we remove the updates from the location listener.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
