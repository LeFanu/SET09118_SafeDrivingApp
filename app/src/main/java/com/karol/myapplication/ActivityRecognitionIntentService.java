package com.karol.myapplication;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

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
    public ActivityRecognitionIntentService(String name) {
        super(name);
    }

    public boolean isDriving;

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            // Get the update
            ActivityRecognitionResult result =
                    ActivityRecognitionResult.extractResult(intent);

            DetectedActivity mostProbableActivity
                    = result.getMostProbableActivity();

            // Get the confidence % (probability)
            int confidence = mostProbableActivity.getConfidence();

            // Get the type
            int activityType = mostProbableActivity.getType();
           /* types:
            * DetectedActivity.IN_VEHICLE
            * DetectedActivity.ON_BICYCLE
            * DetectedActivity.ON_FOOT
            * DetectedActivity.STILL
            * DetectedActivity.UNKNOWN
            * DetectedActivity.TILTING
            */
            // used on-foot for testing to see if app works fine
            if (activityType == 2)
            {
                isDriving = true;
            }
            else
                isDriving = false;
        }
    }
}