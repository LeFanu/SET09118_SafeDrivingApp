package com.karol.myapplication;

import android.content.Context;
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
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);


        if (sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null)
        {
            lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        }
        else
        {
            //Snackbar.make(view, "Sorry no light lightSensor", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        }
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        sensorManager.registerListener(mSensorEventListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    TextView textbox ;
    private SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public final void onSensorChanged(SensorEvent event) {
            float lux = event.values[0];
            textbox = (TextView) findViewById(R.id.lightText);
            // Do something with this sensor value
            //Toast.makeText(getApplicationContext(), "light is  " + lux, Toast.LENGTH_SHORT).show();
            textbox.setText("Light Sensor value is: " + lux);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

}
