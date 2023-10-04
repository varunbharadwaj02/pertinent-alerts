package com.example.pertinentalerts;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import android.telephony.SmsManager;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button button;
    final static int REQUEST_CODE = 1 ;
    Circle circle ;
    public String phno="",msg="";
    LatLng location ;
    public LocationManager lm ;
    double alarm_location_latitude = 0;
    double alarm_location_longitutde = 0;
    double current_location_latitude = 0;
    double current_location_longitutde = 0;
    boolean state = false ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        button=(Button) findViewById(R.id.weather1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openweather();
            }
        });
    }
    public void openweather()
    {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
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
        //current_location_latitude = 32.361114 ;
        //current_location_longitutde = 74.207883 ;

        // Add a marker in Sydney and move the camera
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        getMyLocation();
        location = new LatLng(current_location_latitude, current_location_longitutde);
        mMap.addMarker(new MarkerOptions().position(location).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );

    }
    public  void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        } else {
            Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            current_location_latitude = loc.getLatitude();
            current_location_longitutde = loc.getLongitude();
            // Toast.makeText(getApplicationContext(),current_location_latitude+" , "+ current_location_longitutde , Toast.LENGTH_SHORT).show();
        }
    }
    // Checks whether user is inside of circle or not
    public boolean IsInCircle(){
        float distance[] ={0,0,0};
        Location.distanceBetween( current_location_latitude,current_location_longitutde,
                circle.getCenter().latitude, circle.getCenter().longitude, distance);
        if( distance[0] > circle.getRadius())
            return false;
        else
            return true;
    }
    public void addAlarm(View v){
        //getMyLocation();
        Intent i = new Intent(this, Alarm.class);
        i.putExtra("longitude" ,current_location_longitutde );
        i.putExtra("latitude" ,current_location_latitude );
        startActivityForResult(i, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if (data.hasExtra("alarm_location_latitude") && data.hasExtra("alarm_location_longitude")) {
                state = true;
                alarm_location_latitude = data.getExtras().getDouble("alarm_location_latitude");
                alarm_location_longitutde = data.getExtras().getDouble("alarm_location_longitude");
                phno=data.getExtras().getString("phone_number");
                msg=data.getExtras().getString("Txt_msg");
                location = new LatLng(current_location_latitude,current_location_longitutde);
                mMap.addMarker(new MarkerOptions().position(location).title("Alarm Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                // Add a circle of radius 50 meter
                circle = mMap.addCircle(new CircleOptions()
                        .center(new LatLng(current_location_latitude, current_location_longitutde))
                        .radius(50).strokeColor(Color.RED).fillColor(Color.BLUE));

                //--------------- Check user is in Range or Not after 5 Seconds --------
                final Handler handler = new Handler();
                final int delay = 5000; //milliseconds
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //do something
                        getMyLocation();
                        if (IsInCircle()) {
                            if (state == true) {

                                SmsManager smstext =SmsManager.getDefault();

                                smstext.sendTextMessage(phno,null,msg,null,null);
                                Intent intent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                        getApplicationContext(), 234324243, intent, 0);
                                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                                        + (100), pendingIntent);
                                state = false;
                            }
                        }
                        handler.postDelayed(this, delay);
                    }
                }, delay);


            }
        }
    }
}