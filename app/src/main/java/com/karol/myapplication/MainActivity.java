package com.karol.myapplication;

import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;


public class MainActivity extends AppCompatActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //fields used in GUI
    private TextView speedTextbox;
    private TextView drivingTimeHours;
    private TextView drivingTimeMinutes;
    String TAG = "Karol";

    //Sensors properties
    private SensorManager sensorManager;
    private LocationManager locationManager;
    private Location previousLocation = null;
    private float distanceBetweenNewAndOldLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private Sensor accelerometerSensor;
    private ActivityRecognitionClient activityRecognition; //used for checking if phone is in a driving car
    private String currentActivity;
    private PendingIntent pendingIntent;
    private GoogleApiClient googleApiClient;
    private GoogleApiClient activityRecognitionGoogleApiClient;

    private ActivityRecognitionIntentService activityDetection = new ActivityRecognitionIntentService("Is It Driving");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //@Karol: accessing Driving Status textview. Required only when on the move
        /*TextView drivingStatusIndicator = (TextView) findViewById(R.id.driving_status_indicator);
        drivingStatusIndicator.setText("OK");
        drivingStatusIndicator.setTextColor(getResources().getColor(R.color.driving_ok));*/

        speedTextbox = (TextView) findViewById(R.id.speed_value);


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //using accelerometer
        registerAccelerometerListener();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //@Karol: Apparently needed for getLastLocation()
        //Unfortunately it has a return so leaves the method once reaches that point
       /* if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "Permissions granted");
            return;
        }*/
/*        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object
                            float speed = location.getSpeed();
                            Log.d(TAG,"Speed is " + speed);
                        }
                    }
                });*/

        /*if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        createLocationRequest();
*/
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        googleApiClient.connect();

        //activityRecognition = ActivityRecognition.getClient(this);



        String isDriving = String.valueOf(activityDetection.isDriving);
        speedTextbox.setText(isDriving);
    }



    //@Karol - Extracted this method to avoid copying lines
    private void registerAccelerometerListener()
    {
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        final double alpha = 0.8;

        double[] gravity = new double[3];
        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        double[] linear_acceleration = new double[3];
        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];
        //Log.v(TAG, "Accelerometer values are " + linear_acceleration[0] + " " + linear_acceleration[1] + " " + linear_acceleration[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        //stopLocationUpdates();

        registerAccelerometerListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        /*if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }*/


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Log.d("Karol", "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        //startLocationUpdates();
        Intent intent = new Intent( this, MainActivity.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( googleApiClient, 3000, pendingIntent );

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TAG is the name you use to filter the log results. It can be anything
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }




  /*
    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }


    @Override
    public void onLocationChanged(Location location) {
        if(previousLocation!=null){
            //distanceBetweenNewAndOldLocation = location.distanceTo(previousLocation);
            distanceBetweenNewAndOldLocation = location.getSpeed();
            speedTextbox.setText("Speed :" + distanceBetweenNewAndOldLocation + "m/s");
            Toast.makeText(getApplicationContext(),"Location changed",Toast.LENGTH_SHORT);

Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        }
    }*/


/*
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        Log.d(TAG, "Location update stopped .......................");
    }
*/

    /*private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }*/

   /* mFusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
        @Override
        public void onSuccess(Location location) {
            // Got last known location. In some rare situations this can be null.
            if (location != null) {
                // Logic to handle location object
            }
        }
    });*/

   /* protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }*/


    //LocationListener locationListener = new LocationListener() {

}

