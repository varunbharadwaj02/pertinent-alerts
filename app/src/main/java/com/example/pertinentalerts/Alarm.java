package com.example.pertinentalerts;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Alarm extends AppCompatActivity {
    List<Double> tmp=null;
    Button btn ;
    TextView t1 , t2 ;
    EditText latitude_txt , longitude_txt,cityname_txt,editTextNumber,editTextMsg ;
    boolean check = false ;

    List<Double> getlatlong(String cityname){
        List<LatLng> ll = null;
        List<Double> ltlg = new ArrayList<Double>();
        if(Geocoder.isPresent()){
            try {
                String location = cityname;
                Geocoder gc = new Geocoder(this);
                List<Address> addresses= gc.getFromLocationName(location, 1);
                // get the found Address Objects
                ll = new ArrayList<LatLng>(addresses.size()); // A list to save the coordinates if they are available
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        ll.add(new LatLng(a.getLatitude(), a.getLongitude()));
                    }
                }
                System.out.println("printing the locations");
                for(LatLng a:ll ){
                    ltlg.add(a.latitude);
                    ltlg.add(a.longitude);
                    System.out.println(a.latitude+" "+a.longitude);
                }

            }
            catch (IOException e) {
                return null;
                // handle the exception
            }
        }
        else{
            Log.v("nothing","goecoder not present");
        }
        return ltlg;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

//        latitude_txt = (EditText)findViewById(R.id.latitude);
//        longitude_txt = (EditText)findViewById(R.id.longitude);
        cityname_txt = (EditText)findViewById(R.id.cityname);
        editTextNumber=(EditText)findViewById(R.id.phno);
        editTextMsg=(EditText)findViewById(R.id.txtmsg);

        Bundle extras = getIntent().getExtras();
        double lati = extras.getDouble("latitude");
        double longi = extras.getDouble("longitude");
//        latitude_txt.setText(lati+"");
//        longitude_txt.setText(longi+"");

        TextView t = (TextView)findViewById(R.id.text);
        // t.setText(value1+" & "+value2);
    }
    @Override
    public void finish() {

        if(check==true) {




//            double lati = Double.parseDouble(latitude_txt.getText().toString());
//            double longi = Double.parseDouble(longitude_txt.getText().toString());
            String phonenumber=editTextNumber.getText().toString().trim();
            String msg=editTextMsg.getText().toString().trim();
            Double lati=tmp.get(0);
            Double longi=tmp.get(1);
            // Prepare data intent
            Intent i = new Intent();
            i.putExtra("alarm_location_latitude", lati);
            i.putExtra("alarm_location_longitude", longi);
            i.putExtra("phone_number", phonenumber);
            i.putExtra("Txt_msg",msg);

            // Activity finished ok, return the data
            setResult(RESULT_OK, i);
        }
        super.finish();
    }
    public void SetAlram(View v){
        String s=cityname_txt.getText().toString();

        if(!s.isEmpty()) {
            tmp = getlatlong(s);
            if (!tmp.isEmpty()) {
                System.out.println(tmp);
                check = true;
                ToastMsg("Alarm is set for "+s);
                finish();
            } else {
                ToastMsg("Enter the correct Location");
            }
        }
        else{
            ToastMsg("! Please Enter Text ");
        }

    }


    private void ToastMsg(String tomsg) {
        Toast msg = Toast.makeText(getBaseContext(), tomsg, Toast.LENGTH_LONG);
        msg.show();
    }
}