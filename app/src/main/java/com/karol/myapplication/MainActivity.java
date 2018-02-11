package com.karol.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.SensorManager;
import android.hardware.Sensor;
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


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private SensorManager sensorManager;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /*Light sensor*/
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(mSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);

        /*GPS*/

        sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");

        TextView drivingStatusIndicator = (TextView) findViewById(R.id.driving_status_indicator);
        drivingStatusIndicator.setText("OK");
        drivingStatusIndicator.setTextColor(getResources().getColor(R.color.driving_ok));

        speedTextbox = (TextView) findViewById(R.id.speed_value);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
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
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,connectionResult.getErrorMessage(),Toast.LENGTH_LONG).show();
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

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    /*
    * End of speed calculator code
    * */

};

