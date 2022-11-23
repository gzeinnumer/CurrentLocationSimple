package com.gzeinnumer.currentlocationsimple;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivityInterval extends AppCompatActivity {
    
    protected GetCurrentLocationInterval currentLocation;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final String TAG = "BaseActivityInterval";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check for the integer request code originally supplied to startResolutionForResult().
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(TAG, "User agreed to make required location settings changes.");
                    // Nothing to do. startLocationupdates() gets called in onResume again.
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(TAG, "User chose not to make required location settings changes.");
                    currentLocation.setMRequestingLocationUpdates();
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (currentLocation != null) {
            if (currentLocation.getmRequestingLocationUpdates() && currentLocation.checkPermissions()) {
                currentLocation.startLocationAction();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (currentLocation != null) {
            if (currentLocation.getmRequestingLocationUpdates()) {
                currentLocation.stopLocationAction();
            }
        }
    }

}
