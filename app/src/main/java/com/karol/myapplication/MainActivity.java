package com.karol.myapplication;

import android.Manifest;
<<<<<<< HEAD
import android.content.Context;
=======
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.location.Location;
<<<<<<< HEAD
=======
import android.location.LocationManager;
>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
<<<<<<< HEAD
import android.hardware.SensorManager;
import android.hardware.Sensor;
=======
import android.util.Log;
>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726
import android.widget.TextView;

<<<<<<< HEAD

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
=======
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726

public class MainActivity extends AppCompatActivity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

<<<<<<< HEAD
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
=======
    //fields used in GUI
    private TextView speedTextbox;
    private TextView drivingTimeHours;
    private TextView drivingTimeMinutes;
    String TAG = "Karol";
>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726

    //Sensors properties
    private SensorManager sensorManager;
<<<<<<< HEAD
    private Sensor lightSensor;

    private TextView speedTextbox;

    private Location previousLocation = null;
    private Location currentLocation = null;
    private String mCurrentLocationUpdateTime = null;
    private String mpreviousLocationUpdateTime = null;
    private SimpleDateFormat sdf;
    private Date datePreviousLocation;
    private Date dateCurrentLocation;
    long differenceTime = 0;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
=======
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
>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

<<<<<<< HEAD
        /*Light sensor*/
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(mSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        /*GPS*/

        sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        TextView drivingStatusIndicator = (TextView) findViewById(R.id.driving_status_indicator);
=======
        //@Karol: accessing Driving Status textview. Required only when on the move
        /*TextView drivingStatusIndicator = (TextView) findViewById(R.id.driving_status_indicator);
>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726
        drivingStatusIndicator.setText("OK");
        drivingStatusIndicator.setTextColor(getResources().getColor(R.color.driving_ok));*/

        speedTextbox = (TextView) findViewById(R.id.speed_value);

<<<<<<< HEAD
        speedTextbox = (TextView) findViewById(R.id.speed_value);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

=======

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
>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
<<<<<<< HEAD

        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1500);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

    }
    /*
    * END ON CREATE
    * */

    /**
     * Light sensor code
     */
    TextView textbox;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public final void onSensorChanged(SensorEvent event) {
            float lux = event.values[0];
            textbox = (TextView) findViewById(R.id.visibility_value);
            // Do something with this sensor value
            //Toast.makeText(getApplicationContext(), "light is  " + lux, Toast.LENGTH_SHORT).show();
            textbox.setText(String.valueOf(lux));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    /**
     * End of light sensor code
     */
    /*
    * Speed calculator code
    * */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
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
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
=======

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

>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
<<<<<<< HEAD
        Toast.makeText(this,connectionResult.getErrorMessage(),Toast.LENGTH_LONG).show();
=======
        //TAG is the name you use to filter the log results. It can be anything
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726
    }




  /*
    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }


    @Override
    public void onLocationChanged(Location location) {
        previousLocation = currentLocation;
        mpreviousLocationUpdateTime = mCurrentLocationUpdateTime;
        currentLocation = location;
        mCurrentLocationUpdateTime = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis()));

        if((mpreviousLocationUpdateTime!=null)&&(mCurrentLocationUpdateTime!=null)){
            try {
                datePreviousLocation = sdf.parse(mpreviousLocationUpdateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

<<<<<<< HEAD
            try {
                dateCurrentLocation = sdf.parse(mCurrentLocationUpdateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            differenceTime = dateCurrentLocation.getTime() - datePreviousLocation.getTime();
            differenceTime = differenceTime/1000;//millisec to seconds
           // Toast.makeText(this, String.valueOf(differenceTime), Toast.LENGTH_SHORT).show();//Should display value arround 3

        }else{
            differenceTime = 0;
        }

        if((previousLocation==null)||(currentLocation==null)){
            speedTextbox.setText("Calibrating...");
        } else {
            double distance = currentLocation.distanceTo(previousLocation);

            double speed;
            if(differenceTime!=0){
                speed= Math.round((distance/differenceTime)*100.0)/100.0;
            }else{
                speed= 0.0;
            }
            speedTextbox.setText(Double.toString(speed));

        }
        //Toast.makeText(this, "Speed updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(googleApiClient.isConnected()){
            requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
    }
=======
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
>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    /*
    * End of speed calculator code
    * */

}

