package com.gzeinnumer.currentlocationsimple;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;

import com.gzeinnumer.currentlocationsimple.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivityInterval {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // initialize the necessary libraries
        init();
    }

    private void init() {
        this.currentLocation = new GetCurrentLocationInterval(MainActivity.this, new GetCurrentLocationInterval.FunCallBack() {
            @SuppressLint("SetTextI18n")
            @Override
            public void updateLocation(Location location) {
                if (location != null) {
                    binding.txtLocationResult.setText(
                            "Lat: " + location.getLatitude() +
                                    ",\nLng: " + location.getLongitude() +
                                    ",\nLast updated on: " + currentLocation.getmLastUpdateTime());

                    // giving a blink animation on TextView
                    binding.txtLocationResult.setAlpha(0);
                    binding.txtLocationResult.animate().alpha(1).setDuration(300);
                }
            }

            @Override
            public void isServiceRunning(boolean isServiceRunning) {
                //if    isServiceRunning true   disable btnStart and enable btnStop
                //esle  isServiceRunning false  enable  btnStart and disable btnStop
            }

            @Override
            public void isMock(boolean isMockLocation) {
                binding.txtIsMock.setText("Is Mock : "+isMockLocation);
                if (isMockLocation){
                    binding.txtIsMockFinal.setText("Mock location active, do something");
                }
            }
        });

        //start service
        this.currentLocation.startLocation();

        //stop service
        this.currentLocation.stopLocation();

        if (this.currentLocation.getmCurrentLocation() != null) {
            String lat = this.currentLocation.getmCurrentLocation().getLatitude()+"";
            String lng = this.currentLocation.getmCurrentLocation().getLongitude()+"";
            String time = this.currentLocation.getmLastUpdateTime();
            boolean isMockLocatio = this.currentLocation.isMockLocation();
        }
    }
}