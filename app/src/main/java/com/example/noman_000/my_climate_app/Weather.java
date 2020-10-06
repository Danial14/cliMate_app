package com.example.noman_000.my_climate_app;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Weather {
   private String temp;
   private String weatherSymbol;
   private String city;
   public static Weather fromJSON(JSONObject response){
       Weather weather = new Weather();
       try{
           weather.city = response.getString("name");
           JSONArray array = response.getJSONArray("weather");
           JSONObject jsonObject = array.getJSONObject(0);
           int condition = jsonObject.getInt("id");
           jsonObject = response.getJSONObject("main");
           double temp = jsonObject.getDouble("temp");
           double tempInCentigrade = temp - 273.15;
           int roundTemp = (int) Math.rint(tempInCentigrade);
           weather.temp = String.valueOf(roundTemp);
           weather.weatherSymbol = weather.evaluateWeatherCondition(condition);
           return weather;
       }
       catch (JSONException ex){
           ex.printStackTrace();
       }
       return null;
   }
   private String evaluateWeatherCondition(int condition){
       if (condition >= 0 && condition < 300) {
           return "tstorm1";
       } else if (condition >= 300 && condition < 500) {
           return "light_rain";
       } else if (condition >= 500 && condition < 600) {
           return "shower3";
       } else if (condition >= 600 && condition <= 700) {
           return "snow4";
       } else if (condition >= 701 && condition <= 771) {
           return "fog";
       } else if (condition >= 772 && condition < 800) {
           return "tstorm3";
       } else if (condition == 800) {
           return "sunny";
       } else if (condition >= 801 && condition <= 804) {
           return "cloudy2";
       } else if (condition >= 900 && condition <= 902) {
           return "tstorm3";
       } else if (condition == 903) {
           return "snow5";
       } else if (condition == 904) {
           return "sunny";
       } else if (condition >= 905 && condition <= 1000) {
           return "tstorm3";
       }
       return "dunno";
   }

    public String getTemp() {
        return temp;
    }

    public String getWeatherSymbol() {
        return weatherSymbol;
    }

    public String getCity() {
        return city;
    }
}
