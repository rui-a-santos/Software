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
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
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
import com.google.firebase.database.ChildEventListener;
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
    private double mLng = 0;
    private long steps_walked = 0;
    private SensorManager sManager;
    private Sensor stepSensor;
    private Location mUserLocation = null;
    private Map<String, Marker> mMarkerList = null;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private TextView stepsTextView = null;
    private TextView distanceTextView = null;
    private TextView caloriesTextView = null;
    private User currentUser = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        this.sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        this.mMarkerList = new HashMap<>();
        this.stepsTextView = findViewById(R.id.steps);
        this.distanceTextView = findViewById(R.id.distance);
        this.caloriesTextView = findViewById(R.id.calories);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.database = FirebaseDatabase.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        createStepsListener();
        createUserListener();

        if (!checkRunPermissions()) {
            Log.v("start", "started");
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            startService(intent);
        }

        DatabaseReference steps = this.database.getReference("Users").child(user.getUid());
        steps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user != null) {
                    currentUser = user;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());

            }
        });

    }

    private void createStepsListener() {
        DatabaseReference steps = this.database.getReference("Users").child(user.getUid()).child("steps");
        steps.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long post = 0;
                if(dataSnapshot.getValue()!=null) {
                    post = (long)dataSnapshot.getValue();
                }
                if(post != 0) {
                    Log.v("Database steps:", String.valueOf(post));
                    steps_walked = post;
                    long distanceRun = getDistanceRun(steps_walked);
                    stepsTextView.setText(steps_walked + " Steps Walked");
                    distanceTextView.setText(distanceRun + " Metres Travelled");
                    caloriesTextView.setText(calculateCaloriesBurnt(distanceRun) + " Calories Burnt");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                long distanceRun = getDistanceRun(steps_walked);
                stepsTextView.setText("Steps walked : " +  steps_walked);
                stepsTextView.setText(steps_walked + " Steps Walked");
                distanceTextView.setText(distanceRun + " Metres Travelled");
                caloriesTextView.setText(calculateCaloriesBurnt(distanceRun) + " Calories Burnt");
            }
        });
    }

    private void createUserListener() {
        DatabaseReference ref = this.database.getReference("Users");

        ref.addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User u = dataSnapshot.getValue(User.class);
                double lat = u.getLat();
                double lng = u.getLng();
                if(lat == 0 && lng == 0) return;
                LatLng loc = new LatLng(lat, lng);
                if(mMarkerList.containsKey(u.getId())) {
                    for ( Map.Entry<String, Marker> entry : mMarkerList.entrySet()) {
                        String key = entry.getKey();
                        Log.v("Help me", key + entry.getValue().getPosition());
                        if(key == u.getId()) {
                            Log.v("I AM HERE", "I AM HERE");
                            entry.getValue().setPosition(loc);
                            break;
                        }
                        // do something with key and/or tab
                    }
                } else {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(loc).title(u.getFirstName() + " " + u.getLastName()));
                    Log.v("FML", "FML");
                    mMarkerList.put(u.getId(), marker);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User u = dataSnapshot.getValue(User.class);
                double lat = u.getLat();
                double lng = u.getLng();
                LatLng loc = new LatLng(lat, lng);
                if(mMarkerList.containsKey(u.getId())) {
                    for ( Map.Entry<String, Marker> entry : mMarkerList.entrySet()) {
                        String key = entry.getKey();
                        Log.v("Help me", key + entry.getValue().getPosition());
                        if(key == u.getId()) {
                            Log.v("I AM HERE", "I AM HERE");
                            entry.getValue().setPosition(loc);
                            break;
                        }
                        // do something with key and/or tab
                    }
                } else {
                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(loc).title(u.getFirstName() + " " + u.getLastName()));
                    Log.v("FML", "FML");
                    mMarkerList.put(u.getId(), marker);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
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

                    //Setting location for the camera.
                    mLocation = new LatLng(mLat, mLng);
                    mCameraUpdate = CameraUpdateFactory.newLatLngZoom(mLocation, 16);
                    //If firstUpdate is true then this is the first location update we are receiving so we have to zoom to the users location.
                    if (mFirstUpdate) {
                        mGoogleMap.animateCamera(mCameraUpdate);
                        mFirstUpdate = false;
                    }
                }
            };
        }
        registerReceiver(mBroadcastReceiver, new IntentFilter("locationUpdate"));
        sManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble("steps", this.steps_walked);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if(sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps_walked++;
            DatabaseReference ref = database.getReference("Users").child(user.getUid()).child("steps");
            ref.setValue(steps_walked);
        }
    }

    //Multiply steps by average height of a male (78cm). Divide by 100 to get answer in metres.
    public long getDistanceRun(long steps) {
        long distance = (steps*78)/100;
        return distance;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int calculateCaloriesBurnt(float distance) {
        if(currentUser != null) {
            Log.v("Weight", String.valueOf(currentUser.getWeight()));
            double calPerMile = 0.5 * currentUser.getWeight();
            double distanceInMiles = distance * 0.000621371;
            Log.v("distance", String.valueOf(distanceInMiles));
            return (int)(calPerMile*distanceInMiles);
        }
        return 0;
    }

}
