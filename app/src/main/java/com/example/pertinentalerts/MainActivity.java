package com.example.pertinentalerts;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    EditText etCity,etCountry;
    TextView tvResult;
    Button button;
    ImageView weather;
    private final String url="http://api.openweathermap.org/data/2.5/weather";
    private final String appid="b2fe2cbbf17935f14f5161d89fa84961";
    DecimalFormat df=new DecimalFormat("#.##");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etCity=findViewById(R.id.etCity);
        etCountry=findViewById(R.id.etCountry);
        tvResult = findViewById(R.id.tvResult);
        weather=findViewById(R.id.weathericon);

    }

    public void getweatherDetails(View view) {
        String tempurl="";
        String city=etCity.getText().toString().trim();
        String country=etCountry.getText().toString().trim();
        if(city.equals("")){
            tvResult.setText("City Field cannot be empty!");
        }
        else
        {
            if(!country.equals(""))
                tempurl=url+"?q="+city+","+country+"&appid="+appid;
            else
                tempurl=url+"?q="+city+"&appid="+appid;
            StringRequest stringRequest=new StringRequest(Request.Method.POST, tempurl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d("response",response);
                    String output="";
                    try {
                        JSONObject jsonResponse=new JSONObject(response);
                        int idn=jsonResponse.getJSONArray("weather").getJSONObject(0).getInt("id");
                        JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                        JSONObject jsonObjectWeather=jsonArray.getJSONObject(0);
                        String description=jsonObjectWeather.getString("description");
                        JSONObject jsonObjectMain=jsonResponse.getJSONObject("main");
                        double temp=jsonObjectMain.getDouble("temp")-273.15;
                        double feelslike=jsonObjectMain.getDouble("feels_like")-273.15;
                        float pressure=jsonObjectMain.getInt("pressure");
                        int humidity=jsonObjectMain.getInt("humidity");
                        JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                        String wind=jsonObjectWind.getString("speed");
                        JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                        String clouds=jsonObjectClouds.getString("all");
                        JSONObject jsonObjectSys=jsonResponse.getJSONObject("sys");
                        String countryName=jsonObjectSys.getString("country");
                        String cityName=jsonResponse.getString("name");
                        tvResult.setTextColor(Color.rgb(68,134,150));
                        int resourceID=getResources().getIdentifier(updateWeatherIcon(idn),"drawable",getPackageName());
                        weather.setImageResource(resourceID);
                        output+="Current weather of "+cityName+" ("+countryName+") "
                                +"\nTemperature:  "+df.format(temp)+"°C"
                                //+"\nFeels Like:  "+df.format(feelslike)+"°C"
                               // +"\nHumidity" +humidity+"%"
                                +"\nDescription:  " +description
                                +"\nWind Speed:   "+wind+"m/s(meters per second)";
                               // +"\nCloudiness"+clouds+"%"
                               // +"\nPressure"+pressure+"hPa";
                        tvResult.setText(output);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(),"Please enter valid city name",Toast.LENGTH_SHORT).show();
                }
            });
            RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(stringRequest);
        }

    }
    public static String updateWeatherIcon(int id)
    {
        if(id>=0&&id<=300) {
            return "thunderstorm";
        }
        else if(id>=300&&id<=500) {
            return "showerrain";
        }
        else if(id>=500&&id<=600) {
            return "rain";
        }
        else if(id>=600&&id<=700) {
            return "snow";
        }
        else if(id>=701 &&id<=771) {
            return "mist";
        }
        else if(id>=772&&id<=800) {
            return "clearsky";
        }
        else if(id>=801&&id<=804) {
            return "fewclouds";
        }
        else if(id>=805) {
            return "scatteredclouds";
        }
        return "dont know";
    }
}