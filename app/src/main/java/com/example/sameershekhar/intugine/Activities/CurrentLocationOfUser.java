package com.example.sameershekhar.intugine.Activities;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.sameershekhar.intugine.R;
import com.example.sameershekhar.intugine.Services.GetGPSLocationAndSendToServer;
import com.example.sameershekhar.intugine.Networking.VolleyGetRequestFromServer;
import com.example.sameershekhar.intugine.Utils.Constant;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CurrentLocationOfUser extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private int interval;
    private LatLng currentPosition;
    private BroadcastReceiver broadcastReceiver;

    public interface VolleyCallback{
        void onSuccess(int interval);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location_of_user);
       // Intent intent = new Intent(this,GetGPSLocationAndSendToServer.class);
        //intent.putExtra(Constant.INTERVAL,interval);
        //startService(intent);

        //asking for runtime permission
        if(!runtime_Permission())
        {
            callServer();
        }
        else
        {
            runtime_Permission();
        }
        //setting google map here
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver==null)
        {
            broadcastReceiver=new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.v("hello","In onReceive  of bc");
                    Log.v("onReceiveLatAndLong",intent.getExtras().get(Constant.LATITUDE)+" "+intent.getExtras().get(Constant.LOGITUDE));
                    currentPosition=new LatLng((double)intent.getExtras().get(Constant.LATITUDE),(double)intent.getExtras().get(Constant.LOGITUDE));
                    loadMap();
                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter(Constant.INTENTFILTER));
    }



    private boolean runtime_Permission()
    {
        if(Build.VERSION.SDK_INT>=23&& ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)!=
                PackageManager.PERMISSION_GRANTED )
        {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    100);
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==100)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED)
            {
                callServer();
            }
            else
            {
                runtime_Permission();
            }
        }
    }

    public void loadMap()
    {


        // Add a marker in at current user location and move the camera
        //LatLng currentPos = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(currentPosition));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new MarkerOptions().position(currentPosition).getPosition(), 20));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPosition));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver!=null)
            unregisterReceiver(broadcastReceiver);
    }


    private void callServer()
    {
        //startService(new Intent(CurrentLocationOfUser.this, GetGPSLocationAndSendToServer.class));
        VolleyGetRequestFromServer.fetchIntervalFromServer(this,Constant.URL_OF_GETTING_INTERVAL,new VolleyCallback(){
            @Override
            public void onSuccess(int interval) {

                if(interval==0)
                {
                    Toast.makeText(getApplicationContext(),"Error from server",Toast.LENGTH_LONG).show();
                }
                else {
                    Log.v("IntervalOnMain",interval+"");
                    Intent intent = new Intent(getApplicationContext(), GetGPSLocationAndSendToServer.class);
                    //intent.putExtra(Constant.INTERVAL,interval);
                    startService(intent);
                    Log.v("StartingService","Starting service");

                }
            }

        });
    }
}
