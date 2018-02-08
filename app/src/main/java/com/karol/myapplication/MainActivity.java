package com.karol.myapplication;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.hardware.SensorManager;
import android.hardware.Sensor;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor lightSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView drivingStatusIndicator = (TextView) findViewById(R.id.driving_status_indicator);
        drivingStatusIndicator.setText("OK");
        drivingStatusIndicator.setTextColor(getResources().getColor(R.color.driving_ok));


//        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
//        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
//
//
//        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null)
//        {
//            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//        }
//        else
//        {
//            //Snackbar.make(view, "Sorry no light lightSensor", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//        }
//        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//        sensorManager.registerListener(mSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }


}
