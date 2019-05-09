package com.example.softwareproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The main activity of the app. This activity is responsible for displaying the map as well as the markers of users.
 */
public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener, SensorEventListener {

    private GoogleMap mGoogleMap = null;
    private BroadcastReceiver mBroadcastReceiver = null;
    private LatLng mLocation = null;
    private CameraUpdate mCameraUpdate = null;
    private boolean mFirstUpdate = true;
    private double mLat = 0;
    private TextView stepsTextView = null;
    private long steps_walked = 0;
    private SensorManager sManager;
    private Sensor stepSensor;
    private double mLng = 0;
    private float mSearchDistance;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private Marker mUserLocationMarker = null;
    private Location mUserLocation = null;
    private Map<String, Marker> mMarkerList = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //If we are receiving a bundle then we have to set the searchDistance field to what it was.
        if (savedInstanceState != null) {
            this.mSearchDistance = savedInstanceState.getFloat("searchDistance", 1500);
            this.mLat = savedInstanceState.getDouble("lat", 0);
            this.mLng = savedInstanceState.getDouble("lng", 0);
            this.steps_walked = savedInstanceState.getLong("steps", 0);
        } else {
            this.mSearchDistance = 1500;
        }
        setContentView(R.layout.activity_map);


        this.sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        this.mMarkerList = new HashMap<>();
        //Setting view to the layout of the activity.
        this.stepsTextView = (TextView) findViewById(R.id.steps);
        if (this.steps_walked != 0) {
            this.stepsTextView.setText("Steps walked : " + this.steps_walked);
        }
        //Initialising map fragment.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.database = FirebaseDatabase.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        // Attach a listener to read the data at our posts reference



            //Checking for permissions, and if we receive permission then start the location service.

        if (!checkRunPermissions()) {
            Log.v("start", "started");
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            startService(intent);
        }

    }


    @Override
    protected void onResume() {
        super.onResume();

        //If the broadcast receiver has not been set then we set it.
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    //Receiving the latitude and longitude from the location service.
                    mLat = (double) intent.getExtras().get("lat");
                    mLng = (double) intent.getExtras().get("lng");
                    //Setting the location of the user.
                    mUserLocation = new Location("");
                    mUserLocation.setLatitude(mLat);
                    mUserLocation.setLongitude(mLng);
                    DatabaseReference reference = database.getReference(("Users"));
                    reference.child(user.getUid()).child("lat").setValue(mLat);
                    reference.child(user.getUid()).child("lng").setValue(mLng);
                    if(mMarkerList.get(0) != null) Log.v("Help me", mMarkerList.get(0).getTitle());
                    if(mMarkerList.get(1) != null) Log.v("Help me", mMarkerList.get(1).getTitle());
                    if(mMarkerList.get(2) != null) Log.v("Help me", mMarkerList.get(2).getTitle());


                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Log.v("HELLO", "HELLO");
                                        User u = snapshot.getValue(User.class);
                                        double lat = u.getLat();
                                        double lng = u.getLng();
                                        LatLng loc = new LatLng(lat, lng);
                                        if(mMarkerList.containsKey(u.getEmail())) {
                                            for ( Map.Entry<String, Marker> entry : mMarkerList.entrySet()) {
                                                String key = entry.getKey();
                                                Marker marker = entry.getValue();
                                                Log.v("Help me", key + marker.getPosition());
                                                if(key == u.getEmail()) {
                                                    Log.v("I AM HERE", "I AM HERE");
                                                    marker.setPosition(loc);
                                                    break;
                                                }
                                                // do something with key and/or tab
                                            }
                                        } else {
                                            Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(loc).title(u.getFirstName() + " " + u.getLastName()));
                                            Log.v("FML", "FML");
                                            mMarkerList.put(u.getEmail(), marker);
                                        }


                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });


                    //Setting location for the camera.
                    mLocation = new LatLng(mLat, mLng);
                    mCameraUpdate = CameraUpdateFactory.newLatLngZoom(mLocation, 16);
                    //If firstUpdate is true then this is the first location update we are receiving so we have to zoom to the users location.
                    if (mFirstUpdate) {
                        mGoogleMap.animateCamera(mCameraUpdate);
                        mFirstUpdate = false;
                    }

                    //Add the marker that shows where the user is.
                    addUserMarker();

                }
            };
        }
        registerReceiver(mBroadcastReceiver, new IntentFilter("locationUpdate"));
        sManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);


//        this.running = true;
//        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
//        if(countSensor != null) {
//            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
//        } else {
//            Toast.makeText(this, "Sensor not found!", Toast.LENGTH_SHORT).show();
//        }


    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    protected void onStop() {
        super.onStop();
        sManager.unregisterListener(this, stepSensor);
    }


    private boolean checkRunPermissions() {
        boolean permission = false;
        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 69);
            permission = true;
        }
        return permission;
    }




    private void addUserMarker() {
        //If the user location marker is not null then it has been set and we have to remove it.
        if (this.mUserLocationMarker != null) {
            this.mUserLocationMarker.remove();
        }
        //Add the marker to the map.
        MarkerOptions mo = new MarkerOptions()
                .position(this.mLocation);
        this.mUserLocationMarker = this.mGoogleMap.addMarker(mo);
    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] results) {
//        super.onRequestPermissionsResult(requestCode, permissions, results);
//        if (requestCode == 69) {
//            if (results[0] == PackageManager.PERMISSION_GRANTED && results[1] == PackageManager.PERMISSION_GRANTED) {
//                Intent intent = new Intent(getApplicationContext(), LocationService.class);
//                startService(intent);
//            } else {
//                checkRunPermissions();
//            }
//        }
//    }

    public boolean setSearchDistance(float searchDistance) {
        //Check if the search distance is negative or too large.
        if (searchDistance <= 0) {
            Toast.makeText(getApplicationContext(), "Search distance has to be positive.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (searchDistance > 1000000000) {
            Toast.makeText(getApplicationContext(), "Cannot search for more than 1,000,000,000 metres", Toast.LENGTH_SHORT).show();
            return false;
        }
        //If not then set it.
        this.mSearchDistance = searchDistance;
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Set the field to the map.
        this.mGoogleMap = googleMap;
        this.mGoogleMap.setOnMapLoadedCallback(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            /**
             * Method run when the user clicks the info window displayed when a marker is clicked.
             * @param marker
             */
            @Override
            public void onInfoWindowClick(Marker marker) {
                //Loop through the marker list to find the marker clicked.

            }
        });
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
    }


//    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
//        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
//        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        vectorDrawable.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater(); //Initialising an instance of a MenuInflater.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putFloat("searchDistance", this.mSearchDistance);
        outState.putDouble("lat", this.mLat);
        outState.putDouble("lng", this.mLng);
        outState.putLong("steps", this.steps_walked);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        float[] values = event.values;
        int value = -1;

        if(values.length > 0) {
            value = (int) values[0];
        }

        if(sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps_walked++;
            DatabaseReference ref = database.getReference("Users").child(user.getUid()).child("steps");
            ref.setValue(steps_walked);
        }



            this.stepsTextView.setText("Steps walked : "  + steps_walked + "Distance run : " + getDistanceRun(steps_walked) + " metres");
            Log.v("Steps", String.valueOf(steps_walked));

    }
    //Multiply steps by average height of a male (78cm). Divide by 100 to get answer in metres.
    public float getDistanceRun(long steps) {
        float distance = (float)(steps*78)/(float)100;
        return distance;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
