package com.example.softwareproject;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;

import java.util.ArrayList;
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
    private FloatingActionMenu materialDesignFAM;
    private FloatingActionButton fabChat, fabProfile,
            fabLogout, fabCentre, fabLeaderboads, fabRun;
    private List<LatLng> runPoints;
    private boolean startRun = false;
    private PolylineOptions lineOptions;
    private Polyline line;
    private float startingSteps;
    private float endingSteps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        this.stepsTextView = findViewById(R.id.steps);
        this.distanceTextView = findViewById(R.id.distance);
        this.caloriesTextView = findViewById(R.id.calories);
        this.materialDesignFAM = findViewById(R.id.material_design_android_floating_action_menu);
        this.fabChat = findViewById(R.id.material_design_floating_action_menu_chat);
        this.fabProfile = findViewById(R.id.material_design_floating_action_menu_person);
        this.fabLogout = findViewById(R.id.material_design_floating_action_menu_logout);
        this.fabCentre = findViewById(R.id.material_design_floating_action_menu_centre);
        this.fabLeaderboads = findViewById(R.id.material_design_floating_action_menu_leaderboards);
        this.fabRun = findViewById(R.id.material_design_floating_action_menu_run);

        createFabListeners();

        this.sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        this.mMarkerList = new HashMap<>();
        this.runPoints = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.database = FirebaseDatabase.getInstance();


//        if (database == null) {
//            database.setPersistenceEnabled(true);
//        }
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        createStepsListener();
        createUserListener();
        getUser();

        if (!checkRunPermissions()) {
            Log.v("start", "started");
            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            startService(intent);
        }



    }

    private void createFabListeners() {
        fabChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Profile", "Profile");
                Intent intent = new Intent(MapActivity.this, Chat.class);
                startActivity(intent);
            }
        });
        fabProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("Chat", "Chat");
                Intent intent = new Intent(MapActivity.this, Profile.class);
                startActivity(intent);
            }
        });
        fabLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new AlertDialog.Builder(MapActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(MapActivity.this, Login.class);
                            startActivity(intent);
                            finish();
                        }

                    })
                    .setNegativeButton("No", null)
                    .show();
            }
        });
        fabCentre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocation = new LatLng(mLat, mLng);
                mCameraUpdate = CameraUpdateFactory.newLatLngZoom(mLocation, 16);
                mGoogleMap.animateCamera(mCameraUpdate);

            }
        });

        fabLeaderboads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, Leaderboards.class);
                startActivity(intent);
            }
        });

        fabRun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startRun) {
                    startRun = false;
                    endingSteps = steps_walked;
                    float stepstaken = endingSteps - startingSteps;
                    float startingDistance = getDistanceRun((long)startingSteps);
                    float endingDistance = getDistanceRun((long)endingSteps);
                    float distanceRun = endingDistance-startingDistance;

                    float startingCalories = calculateCaloriesBurnt(startingSteps);
                    float endingCalories = calculateCaloriesBurnt(endingSteps);


                    AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);

                    builder.setMessage("Hope you had a nice run! You ran " + distanceRun + " metres, took " + stepstaken + " steps and burned "
                            + (endingCalories-startingCalories) + " calories!" )
                            .setTitle("Finished a run!");

                    AlertDialog dialog = builder.create();
                    dialog.show();
                    if(line != null) line.remove();
                    line = null;
                    fabRun.setLabelText("Track a run");

                } else {
                    startRun = true;
                    startingSteps = steps_walked;
                    fabRun.setLabelText("End the run");
                }
            }
        });


    }

    private void getUser() {
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
        steps.keepSynced(true);
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
                    stepsTextView.setText(steps_walked + " steps taken");
                    distanceTextView.setText(distanceRun + " metres travelled");
                    if(calculateCaloriesBurnt(distanceRun) == 0) {
                        caloriesTextView.setText("Please begin walking to calculate calories.");
                    }
                    caloriesTextView.setText(calculateCaloriesBurnt(distanceRun) + " calories burned");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
                long distanceRun = getDistanceRun(steps_walked);
                stepsTextView.setText("Steps walked: " +  steps_walked);
                stepsTextView.setText(steps_walked + " steps walked");
                distanceTextView.setText(distanceRun + " metres travelled");
                caloriesTextView.setText(calculateCaloriesBurnt(distanceRun) + " calories burnt");
            }
        });
    }

    private void createUserListener() {
        DatabaseReference ref = this.database.getReference("Users");
        ref.keepSynced(true);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                User u = dataSnapshot.getValue(User.class);
//                double lat = u.getLat();
//                double lng = u.getLng();
//                if(lat == 0 && lng == 0) return;
//                LatLng loc = new LatLng(lat, lng);
//                if(mMarkerList.containsKey(u.getId())) {
//                    for ( Map.Entry<String, Marker> entry : mMarkerList.entrySet()) {
//                        String key = entry.getKey();
//                        Log.v("Help me", key + entry.getValue().getPosition());
//                        if(key == u.getId()) {
//                            Log.v("I AM HERE", "I AM HERE");
//                            entry.getValue().setPosition(loc);
//                            break;
//                        }
//                        // do something with key and/or tab
//                    }
//                } else {
//                    Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(loc).title(u.getFirstName() + " " + u.getLastName()));
//                    Log.v("FML", "FML");
//                    mMarkerList.put(u.getId(), marker);
//                }
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
                            Location currentLocation = new Location("currentLocation");
                            currentLocation.setLatitude(mLat);
                            currentLocation.setLongitude(mLng);

                            Location userLocation = new Location("userLocation");
                            userLocation.setLatitude(u.getLat());
                            userLocation.setLongitude(u.getLng());

                            float distance = currentLocation.distanceTo(userLocation);
                            Log.v("Distance to " + u.getFirstName(), String.valueOf(distance));
                            //Only set the marker to visible if the user is within 10 miles.
                            if(distance < 16093.4) {
                                entry.getValue().setVisible(true);
                                entry.getValue().setPosition(loc);
                                break;
                            } else {
                                entry.getValue().setVisible(false);
                                break;
                            }
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
                    if(startRun) {
                        startRun();
                    }
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

    private void startRun() {
        if(line != null) {
            List<LatLng> points = line.getPoints();
            points.add(new LatLng(mLat, mLng));
            line.setPoints(points);
        } else {
            this.line = mGoogleMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(mLat, mLng), new LatLng(mLat, mLng))
                    .width(5)
                    .color(Color.RED));
        }
//        LatLng position = new LatLng(mLat, mLng);
//        if(!runPoints.isEmpty()) {
//            if(runPoints.get(runPoints.size()-1) != position) {
//                this.runPoints.add(position);
//            }
//        }
//        Log.v("runPoints", runPoints.toString());
////        for(int i = 0; i < this.runPoints.size(); i++) {
////            this.lineOptions = new PolylineOptions().width(5).color(Color.RED).add(runPoints.get(i));
////        }
////        if(this.line != null) {
////            this.line.remove();
////        }
////        mGoogleMap.addPolyline(this.lineOptions);
//        for (int i = 0; i < runPoints.size() - 1; i++) {
//            LatLng src = runPoints.get(i);
//            LatLng dest = runPoints.get(i + 1);
//
//            // mMap is the Map Object
//            mGoogleMap.addPolyline(
//                    new PolylineOptions().add(
//                            new LatLng(src.latitude, src.longitude),
//                            new LatLng(dest.latitude,dest.longitude)
//                    ).width(2).color(Color.BLUE).geodesic(true)
//            );
//        }
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
        this.mGoogleMap.setOnMarkerClickListener(this);
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
                for(Map.Entry<String, Marker> entry : mMarkerList.entrySet()) {
                    String key = entry.getKey();
                    Marker m = entry.getValue();
                    Log.v("MarkerID", marker.getId());
                    Log.v("Marker Values", m.getId());
                    if(marker.getId().equals(m.getId())) {
                        Intent intent = new Intent(MapActivity.this, Profile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", key);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }

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
        outState.putParcelable("user", this.currentUser);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if(sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            steps_walked++;
            DatabaseReference ref = database.getReference("Users").child(user.getUid()).child("steps");
            ref.setValue(steps_walked);
            database.getReference("Steps").child(user.getUid()).setValue(-steps_walked);
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

}
