package com.karol.myapplication;

import android.app.PendingIntent;
import android.app.IntentService;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

/**
 * Created by karol on 11/02/18.
 * Used this tutorial: https://dzone.com/articles/android-activity-recognition
 *
 */

public class ActivityRecognitionIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    protected static final String TAG = ActivityRecognitionIntentService.class.getSimpleName();

    public ActivityRecognitionIntentService(String name) {
        super(TAG);
    }

    public boolean isDriving;
    public String activityRecognitionValue = "Not set";


    private ActivityRecognitionClient activityRecognitionClient;

    @SuppressWarnings("unchecked")
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();


        for (DetectedActivity activity : detectedActivities)
        {
            activityRecognitionValue = "Detected activity is  " + activity.getType() + ", " + activity.getConfidence();
            Log.d("Karol", activityRecognitionValue);
            broadcastActivity(activity);
        }

        /*if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            result =
                    ActivityRecognitionResult.extractResult(intent);

            DetectedActivity mostProbableActivity
                    = result.getMostProbableActivity();

            // Get the confidence % (probability)
            int confidence = mostProbableActivity.getConfidence();

            // Get the type
            int activityType = mostProbableActivity.getType();
            *//**
            * types:
            * DetectedActivity.IN_VEHICLE
            * DetectedActivity.ON_BICYCLE
            * DetectedActivity.ON_FOOT
            * DetectedActivity.STILL
            * DetectedActivity.UNKNOWN
            * DetectedActivity.TILTING
            *//*


            Log.d("Karol", "I have been handling intent");
            if (activityType == 3)
            {
                activityRecognitionValue = "I have been handling intent";
            }
            // used on-foot for testing to see if app works fine
            if (activityType == 2)
            {
                isDriving = true;
            }
            else
                isDriving = false;
        }*/
    }


    private void broadcastActivity(DetectedActivity activity)
    {
        Intent intent = new Intent(Constants.BROADCAST_DETECTED_ACTIVITY);
        intent.putExtra("type", activity.getType());
        intent.putExtra("confidence", activity.getConfidence());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();



    }

}