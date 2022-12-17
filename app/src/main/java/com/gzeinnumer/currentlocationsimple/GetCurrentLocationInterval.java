package com.gzeinnumer.currentlocationsimple;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.karumi.dexter.BuildConfig;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.text.DateFormat;
import java.util.Date;

public class GetCurrentLocationInterval {
    public static final String TAG = "Fun";
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private Boolean mRequestingLocationUpdates;
    private LocationRequest mLocationRequest;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Activity activity;
    private FunCallBack funCallBack;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private boolean isMockLocation = false;

    public void setMRequestingLocationUpdates() {
        mRequestingLocationUpdates = false;
    }

    public Boolean getmRequestingLocationUpdates() {
        return mRequestingLocationUpdates;
    }

    public interface FunCallBack{
        void updateLocation(Location mCurrentLocation);
        void isServiceRunning(boolean isServiceRunning);
        void isMock(boolean isMockLocation);
    }

    public GetCurrentLocationInterval(Activity activity, FunCallBack funCallBack) {
        this.funCallBack = funCallBack;
        this.activity = activity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        mSettingsClient = LocationServices.getSettingsClient(activity);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

                funCallBack.updateLocation(mCurrentLocation);
                if (mCurrentLocation != null) {
                    isMockLocation = mCurrentLocation.isFromMockProvider();
                    funCallBack.isMock(isMockLocation);
                }
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    public void startLocationAction() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(activity, locationSettingsResponse -> {
                    Log.i(TAG, "All location settings are satisfied.");

                    showMessage("Started location updates!");

                    //noinspection MissingPermission
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                    funCallBack.updateLocation(mCurrentLocation);
                    if (mCurrentLocation != null) {
                        isMockLocation = mCurrentLocation.isFromMockProvider();
                        funCallBack.isMock(isMockLocation);
                    }
                })
                .addOnFailureListener(activity, e -> {
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " + "location settings ");
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String errorMessage = "Location settings are inadequate, and cannot be " +
                                    "fixed here. Fix in Settings.";
                            showMessage(errorMessage);
                    }

                    funCallBack.updateLocation(mCurrentLocation);
                    if (mCurrentLocation != null) {
                        isMockLocation = mCurrentLocation.isFromMockProvider();
                        funCallBack.isMock(isMockLocation);
                    }
                });
    }

    private void showMessage(String s) {
        Toast.makeText(activity.getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }


    public void startLocation() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(activity)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationAction();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public void stopLocation() {
        mRequestingLocationUpdates = false;
        stopLocationAction();
    }

    public void stopLocationAction() {
        // Removing location updates
        mFusedLocationClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener(activity, task -> {
            showMessage("Location updates stopped!");
            funCallBack.isServiceRunning(mRequestingLocationUpdates);
        });
    }

    public boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    public String getmLastUpdateTime() {
        return mLastUpdateTime;
    }

    public boolean isMockLocation() {
        return isMockLocation;
    }
}
