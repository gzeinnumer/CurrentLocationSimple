package com.gzeinnumer.currentlocationsimple;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivityLowLevel extends AppCompatActivity {
    
    protected GetCurrentLocationInterval currentLocation;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final String TAG = "BaseActivityInterval";

    private CallBackUpdateLocation callBackUpdateLocation;
    public interface CallBackUpdateLocation{
        void updateLocation(Location location);
    }
    private CallBackIsServiceRunning callBackIsServiceRunning;
    public interface CallBackIsServiceRunning{
        void isServiceRunning(boolean isServiceRunning);
    }
    private CallBackIsMock callBackIsMock;
    public interface CallBackIsMock{
        void isMock(boolean isMockLocation);
    }

    protected void setCallBackUpdateLocation(CallBackUpdateLocation callBackUpdateLocation) {
        this.callBackUpdateLocation = callBackUpdateLocation;
    }

    protected void setCallBackIsServiceRunning(CallBackIsServiceRunning callBackIsServiceRunning) {
        this.callBackIsServiceRunning = callBackIsServiceRunning;
    }

    protected void setCallBackIsMock(CallBackIsMock callBackIsMock) {
        this.callBackIsMock = callBackIsMock;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the necessary libraries
        initLocation();
    }

    private void initLocation() {
        this.currentLocation = new GetCurrentLocationInterval(BaseActivityLowLevel.this, new GetCurrentLocationInterval.FunCallBack() {
            @SuppressLint("SetTextI18n")
            @Override
            public void updateLocation(Location location) {
                if (location != null) {
                    if (callBackUpdateLocation!=null){
                        callBackUpdateLocation.updateLocation(location);
                    }
                }
            }

            @Override
            public void isServiceRunning(boolean isServiceRunning) {
                //if    isServiceRunning true   disable btnStart and enable btnStop
                //esle  isServiceRunning false  enable  btnStart and disable btnStop
                if (callBackIsServiceRunning!=null){
                    callBackIsServiceRunning.isServiceRunning(isServiceRunning);
                }
            }

            @Override
            public void isMock(boolean isMockLocation) {
                if (callBackIsMock!=null){
                    callBackIsMock.isMock(isMockLocation);
                }
            }
        });

        //start service
        this.currentLocation.startLocation();
    }

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
