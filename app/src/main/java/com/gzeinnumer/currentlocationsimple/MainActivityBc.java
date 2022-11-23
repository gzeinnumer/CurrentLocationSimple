//package com.gzeinnumer.currentlocationsimple;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.location.Location;
//import android.os.Bundle;
//import android.util.Log;
//import android.widget.Toast;
//
//import com.gzeinnumer.currentlocationsimple.databinding.ActivityMainBinding;
//
//public class MainActivityBc extends BaseActivityInterval {
//
//    private static final String TAG = MainActivityBc.class.getSimpleName();
//    private ActivityMainBinding binding;
//
//    private static final int REQUEST_CHECK_SETTINGS = 100;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivityMainBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // initialize the necessary libraries
//        init();
//
//        initOnClick();
//    }
//
//    private void initOnClick() {
//        binding.btnStartUpdates.setOnClickListener(view -> {
//            currentLocation.startLocationButtonClick();
//        });
//        binding.btnStopUpdates.setOnClickListener(view -> {
//            currentLocation.stopLocationButtonClick();
//        });
//        binding.btnGetLastLocation.setOnClickListener(view -> {
//            currentLocation.showLastKnownLocation();
//        });
//    }
//
//    private void updateLocationUI(boolean isMockLocation) {
//        if (isMockLocation){
//            Toast.makeText(this, "Fake Location :"+isMockLocation, Toast.LENGTH_SHORT).show();
//        } else {
//            if (currentLocation.getmCurrentLocation() != null) {
//                binding.txtLocationResult.setText(
//                        "Lat: " + currentLocation.getmCurrentLocation().getLatitude() +
//                                ",\nLng: " + currentLocation.getmCurrentLocation().getLongitude()+
//                                ",\nLast updated on: " + currentLocation.getmLastUpdateTime());
//
//                // giving a blink animation on TextView
//                binding.txtLocationResult.setAlpha(0);
//                binding.txtLocationResult.animate().alpha(1).setDuration(300);
//            }
//
//            toggleButtons();
//        }
//    }
//
//    private void toggleButtons() {
//        binding.btnStartUpdates.setEnabled(!currentLocation.getmRequestingLocationUpdates());
//        binding.btnStopUpdates.setEnabled(currentLocation.getmRequestingLocationUpdates());
//    }
//
//    private GetCurrentLocationInterval currentLocation;
//
//    private void init() {
//        currentLocation = new GetCurrentLocationInterval(MainActivityBc.this, new GetCurrentLocationInterval.FunCallBack() {
//            @Override
//            public void updateLocationUIBase(Location location) {
//                boolean isMockLocation = false;
//                if (location != null) {
//                    isMockLocation = location.isFromMockProvider();
//                }
//                updateLocationUI(isMockLocation);
//            }
//
//            @Override
//            public void toggleButtonsBase() {
//                toggleButtons();
//            }
//
//            @Override
//            public void isMock(boolean isMockLocation) {
//
//            }
//        });
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        // Check for the integer request code originally supplied to startResolutionForResult().
//        if (requestCode == REQUEST_CHECK_SETTINGS) {
//            switch (resultCode) {
//                case Activity.RESULT_OK:
//                    Log.i(TAG, "User agreed to make required location settings changes.");
//                    // Nothing to do. startLocationupdates() gets called in onResume again.
//                    break;
//                case Activity.RESULT_CANCELED:
//                    Log.i(TAG, "User chose not to make required location settings changes.");
//                    currentLocation.setMRequestingLocationUpdates();
//                    break;
//            }
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        if (currentLocation != null) {
//            if (currentLocation.getmRequestingLocationUpdates() && currentLocation.checkPermissions()) {
//                currentLocation.startLocationUpdates();
//            }
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (currentLocation != null) {
//            if (currentLocation.getmRequestingLocationUpdates()) {
//                currentLocation.stopLocationUpdates();
//            }
//        }
//    }
//}