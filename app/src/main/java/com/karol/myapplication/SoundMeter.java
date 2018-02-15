package com.karol.myapplication;

import android.media.MediaRecorder;
import android.widget.Toast;

import java.io.IOException;

import static java.lang.Math.log10;

/**
 * Created by alexi_000 on 15/02/2018.
 */

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

public class SoundMeter {

    private AudioRecord ar = null;
    private int minSize;

    public void start() {
        minSize= AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        ar = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,minSize);
        ar.startRecording();
    }

    public void stop() {
        if (ar != null) {
            ar.stop();
        }
    }

    public double getAmplitude() {
        short[] buffer = new short[minSize];
        ar.read(buffer, 0, minSize);
        int max = 0;
        for (short s : buffer)
        {
            if (Math.abs(s) > max)
            {
                max = Math.abs(s);
            }
        }
        double dbvolume = 20 * log10(max);
        dbvolume = Math.round((dbvolume*100.0))/100.0;//Rounding up to 2 digits
        return dbvolume;
    }

}
