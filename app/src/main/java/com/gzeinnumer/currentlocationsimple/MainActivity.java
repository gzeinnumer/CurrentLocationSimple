package com.gzeinnumer.currentlocationsimple;

import android.annotation.SuppressLint;
import android.os.Bundle;

import com.gzeinnumer.currentlocationsimple.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivityLowLevel {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        locationCallBack();
    }

    @SuppressLint("SetTextI18n")
    private void locationCallBack() {
        setCallBackUpdateLocation(location -> {
            if (location != null) {
                binding.txtLocationResult.setText(
                        "Lat: " + location.getLatitude() +
                                ",\nLng: " + location.getLongitude() +
                                ",\nLast updated on: " + currentLocation.getmLastUpdateTime()
                );

                // giving a blink animation on TextView
                binding.txtLocationResult.setAlpha(0);
                binding.txtLocationResult.animate().alpha(1).setDuration(300);
            }
        });
        setCallBackIsServiceRunning(isServiceRunning -> {
            //if    isServiceRunning true   disable btnStart and enable btnStop
            //esle  isServiceRunning false  enable  btnStart and disable btnStop
        });
        setCallBackIsMock(isMockLocation -> {
            binding.txtIsMock.setText("Is Mock : "+isMockLocation);
            if (isMockLocation){
                binding.txtIsMockFinal.setText("Mock location active, do something");
            }
        });

        //u can use like hist, or wait callback give returns
        getDetails();
    }

    private void getDetails() {
        //start service
        //service already running in BaseActivityLowLevel
        //this.currentLocation.startLocation();

        //stop service
//        this.currentLocation.stopLocation();

        if (this.currentLocation!=null && this.currentLocation.getmCurrentLocation() != null) {
            String lat = this.currentLocation.getmCurrentLocation().getLatitude()+"";
            String lng = this.currentLocation.getmCurrentLocation().getLongitude()+"";
            String time = this.currentLocation.getmLastUpdateTime();
            boolean isMockLocatio = this.currentLocation.isMockLocation();
        }
    }
}