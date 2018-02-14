package com.karol.myapplication;

import android.Manifest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.location.Location;

import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    //fields used in GUI
    private TextView drivingTimeHours;
    private TextView drivingTimeMinutes;
    String TAG = "Karol";

    //Sensors properties
    private SensorManager sensorManager;
    private Sensor lightSensor;

    private TextView speedTextbox;
    private TextView drivingStatusIndicator;

    private Location currentLocation = null;
    private String mCurrentLocationUpdateTime = null;
    private String mpreviousLocationUpdateTime = null;
    private SimpleDateFormat simpleDateFormat;
    private Date datePreviousLocation;
    private Date dateCurrentLocation;
    long differenceTime = 0;

    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi; //this looks like some deprecated stuff
    private LocationManager locationManager;
    private Location previousLocation = null;
    //private float distanceBetweenNewAndOldLocation;
    private FusedLocationProviderClient mFusedLocationClient;
    private Sensor accelerometerSensor;
    private ActivityRecognitionClient activityRecognition; //used for checking if phone is in a driving car
    //private String currentActivity;
    //private PendingIntent pendingIntent;
    private GoogleApiClient googleApiClient;
    //FOR TESTING Activity Recognition
    private         BroadcastReceiver broadcastReceiver;
    private Double myLatitude;
    private Double myLongitude;
    private TextView txtActivity, txtConfidence;
    private ImageView imgActivity;
    private Button btnStartTrcking, btnStopTracking;

    private ActivityRecognitionIntentService activityDetection = new ActivityRecognitionIntentService("Karol");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Light sensor*/
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
        //TODO the if statement is the same. Why not have it in the same if?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            sensorManager.registerListener(mSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        /**
         * GPS ===============================================
         * */

        simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        drivingStatusIndicator = (TextView) findViewById(R.id.driving_status_indicator);
        //@Karol: accessing Driving Status textview. Required only when on the move
        /*drivingStatusIndicator.setText("OK");
        drivingStatusIndicator.setTextColor(getResources().getColor(R.color.driving_ok));*/

        speedTextbox = (TextView) findViewById(R.id.speed_value);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //using accelerometer
        }
        //registerAccelerometerListener();


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1500);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        //TESTING

        txtActivity = (TextView) findViewById(R.id.txt_activity);
        txtConfidence = (TextView) findViewById(R.id.txt_confidence);
        imgActivity = (ImageView) findViewById(R.id.img_activity);
        btnStartTrcking = (Button) findViewById(R.id.btn_start_tracking);
        btnStopTracking = (Button) findViewById(R.id.btn_stop_tracking);

        btnStartTrcking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTracking();
            }
        });

        btnStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTracking();
            }
        });

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Constants.BROADCAST_DETECTED_ACTIVITY)) {
                    int type = intent.getIntExtra("type", -1);
                    int confidence = intent.getIntExtra("confidence", 0);
                    handleUserActivity(type, confidence);
                }
            }
        };

        startTracking();
    }
    /**
    * END ON CREATE
    * */

    private void handleUserActivity(int type, int confidence) {
        String label = getString(R.string.activity_unknown);
        int icon = R.drawable.ic_still;

        switch (type) {
            case DetectedActivity.IN_VEHICLE: {
                label = getString(R.string.activity_in_vehicle);
                icon = R.drawable.ic_driving;
                break;
            }
            case DetectedActivity.ON_BICYCLE: {
                label = getString(R.string.activity_on_bicycle);
                icon = R.drawable.ic_on_bicycle;
                break;
            }
            case DetectedActivity.ON_FOOT: {
                label = getString(R.string.activity_on_foot);
                icon = R.drawable.ic_walking;
                break;
            }
            case DetectedActivity.RUNNING: {
                label = getString(R.string.activity_running);
                icon = R.drawable.ic_running;
                break;
            }
            case DetectedActivity.STILL: {
                label = getString(R.string.activity_still);
                break;
            }
            case DetectedActivity.TILTING: {
                label = getString(R.string.activity_tilting);
                icon = R.drawable.ic_tilting;
                break;
            }
            case DetectedActivity.WALKING: {
                label = getString(R.string.activity_walking);
                icon = R.drawable.ic_walking;
                break;
            }
            case DetectedActivity.UNKNOWN: {
                label = getString(R.string.activity_unknown);
                break;
            }
        }

        Log.e(TAG, "User activity: " + label + ", Confidence: " + confidence);

        if (confidence > Constants.CONFIDENCE) {
            txtActivity.setText(label);
            txtConfidence.setText("Confidence: " + confidence);
            imgActivity.setImageResource(icon);
        }
    }


    /**
     * Light sensor code
     */
    TextView textbox;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public final void onSensorChanged(SensorEvent event) {
            float lux = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
                lux = event.values[0];
            }
            textbox = (TextView) findViewById(R.id.visibility_value);
            // Do something with this sensor value
            textbox.setText(String.valueOf(lux));
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };
    /**
     * End of light sensor code
     */


    /**
     * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    * Speed calculator code
    * */

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

        googleApiClient.connect();

        activityRecognition = ActivityRecognition.getClient(this);

        String isDriving = String.valueOf(activityDetection.isDriving);
        speedTextbox.setText(isDriving);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
        Log.d("Karol", "onConnected - isConnected ...............: " + googleApiClient.isConnected());
        //startLocationUpdates();
        Intent intent = new Intent( this, MainActivity.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( googleApiClient, 3000, pendingIntent );

        drivingStatusIndicator = (TextView) findViewById(R.id.driving_status_indicator);
        //@Karol: accessing Driving Status textview. Required only when on the move
        drivingStatusIndicator.setText(activityDetection.activityRecognitionValue);
        drivingStatusIndicator.setTextColor(getResources().getColor(R.color.driving_ok));
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

        //For Activity Recognition
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.BROADCAST_DETECTED_ACTIVITY));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);

        //For Activity Recognition
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this,connectionResult.getErrorMessage(),Toast.LENGTH_LONG).show();
        //TAG is the name you use to filter the log results. It can be anything
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        //code to check the connection and changes
        myLatitude = location.getLatitude();
        myLongitude = location.getLongitude();
        TextView latitudeText = (TextView) findViewById(R.id.latitude);
        latitudeText.setText("Latitude: " + String.valueOf(myLatitude));
        Log.d(TAG, "Latitude: " + String.valueOf(myLatitude));
        TextView longitudeText = (TextView) findViewById(R.id.longitude);
        longitudeText.setText("Longitude: " + String.valueOf(myLongitude));
        Log.d(TAG, "Longitude: " + String.valueOf(myLongitude));

        //Location code
        previousLocation = currentLocation;
        mpreviousLocationUpdateTime = mCurrentLocationUpdateTime;
        currentLocation = location;
        mCurrentLocationUpdateTime = new SimpleDateFormat("dd-M-yyyy hh:mm:ss").format(new Date(System.currentTimeMillis()));

        if((mpreviousLocationUpdateTime!=null)&&(mCurrentLocationUpdateTime!=null)){
            try {
                datePreviousLocation = simpleDateFormat.parse(mpreviousLocationUpdateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            try {
                dateCurrentLocation = simpleDateFormat.parse(mCurrentLocationUpdateTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            differenceTime = dateCurrentLocation.getTime() - datePreviousLocation.getTime();
            differenceTime = differenceTime/1000;//millisec to hours
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
                speed=(distance/differenceTime);//speed in m/s
                speed=speed*3.6;//speed in km/h
                speed= Math.round(speed*100.0)/100.0;//rounding up at 2 digit
            }else{
                speed= 0.0;
            }
            speedTextbox.setText(Double.toString(speed));
            //Toast.makeText(this, Double.toString(speed), Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(this, "Speed updated", Toast.LENGTH_SHORT).show();
    }

    /*
    * End of speed calculator code
    * */

    //For activity recognition service
    private void startTracking() {
        Intent intent1 = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        startService(intent1);
        drivingStatusIndicator.setText("Tracking is running");
    }

    //For activity recognition service
    private void stopTracking() {
        Intent intent = new Intent(MainActivity.this, BackgroundDetectedActivitiesService.class);
        stopService(intent);
        drivingStatusIndicator.setText("Tracking stopped");

    }
}

