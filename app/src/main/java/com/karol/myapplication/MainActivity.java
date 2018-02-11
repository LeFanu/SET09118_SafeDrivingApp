package com.karol.myapplication;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.app.PendingIntent;
import android.content.Intent;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    //fields used in GUI
    private TextView drivingTimeHours;
    private TextView drivingTimeMinutes;
    String TAG = "Karol";

    //Sensors properties
    private SensorManager sensorManager;
    private Sensor lightSensor;

    private TextView speedTextbox;

    private Location currentLocation = null;
    private String mCurrentLocationUpdateTime = null;
    private String mpreviousLocationUpdateTime = null;
    private SimpleDateFormat sdf;
    private Date datePreviousLocation;
    private Date dateCurrentLocation;
    long differenceTime = 0;

    private LocationRequest locationRequest;
    private FusedLocationProviderApi locationProviderApi = LocationServices.FusedLocationApi;
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

        /*Light sensor*/
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            sensorManager.registerListener(mSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        /*GPS*/

        sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        TextView drivingStatusIndicator = (TextView) findViewById(R.id.driving_status_indicator);
        //@Karol: accessing Driving Status textview. Required only when on the move
        /*TextView drivingStatusIndicator = (TextView) findViewById(R.id.driving_status_indicator);
>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726
        drivingStatusIndicator.setText("OK");
        drivingStatusIndicator.setTextColor(getResources().getColor(R.color.driving_ok));*/

        speedTextbox = (TextView) findViewById(R.id.speed_value);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

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
            float lux = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.CUPCAKE) {
                lux = event.values[0];
            }
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

        //activityRecognition = ActivityRecognition.getClient(this);



        String isDriving = String.valueOf(activityDetection.isDriving);
        speedTextbox.setText(isDriving);
    }



    //@Karol - Extracted this method to avoid copying lines
    /*private void registerAccelerometerListener()
    {
        sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }*/
    /*
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

    }*/

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        requestLocationUpdates();
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

   /* @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Log.d("Karol", "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        //startLocationUpdates();
        Intent intent = new Intent( this, MainActivity.class );
        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( googleApiClient, 3000, pendingIntent );

>>>>>>> fc775aaaf96215324a1014ecefd8c6e4790f1726
    }*/

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

            try {
                dateCurrentLocation = sdf.parse(mCurrentLocationUpdateTime);
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

}

