package com.example.meteocanada;


import android.util.Log;

import com.example.meteocanada.Models.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class WeatherDataParser {
    public static ArrayList<Weather> weathers = new ArrayList<>();

    //This is for debug puposes
    public static String getData(InputStream stream){
        return ParseData(stream);
    }

    public static Weather getData(InputStream stream, String city) {
        Weather weather = new Weather();
       try{
           Gson gson = new Gson();
           weather = gson.fromJson(ParseData(stream),Weather.class);

       }catch (Exception e){
           Log.e("ERROR: ", e.getMessage());
           Log.e("ERROR: ", e.getStackTrace().toString());
       }
       weather.city = city;
       weathers.add(weather);
       return weather;
    }

    private static String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[500000];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {

        }
        return outputStream.toString();
    }
    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }
    public static Weather getWeatherDataByTownName(String name){
        Weather weatherToReturn = null;
        if(!name.contains("_")){
            if(Locale.getDefault().getLanguage() == "en" ){
                name = name + "_e";
            }else{
                name = name + "_f";
            }
        }
        for (Weather weather:weathers) {
            if(weather.city.equals(name)){
                weatherToReturn = weather;
            }
        }
        return weatherToReturn;
    }
    private static String ParseData(InputStream stream){
        String jsString = null;
        try{
            String sxml = readTextFile(stream);
            JSONObject json = XML.toJSONObject(sxml);
            json.getJSONObject("siteData").remove("xmlns:xsi");
            json.getJSONObject("siteData").remove("xsi:noNamespaceSchemaLocation");
            json.getJSONObject("siteData").remove("license");
            JSONArray arr = json.getJSONObject("siteData").getJSONObject("forecastGroup").getJSONArray("forecast");
            for(int i = 0 ; i < arr.length(); i ++){
                arr.getJSONObject(i).remove("winds");
                arr.getJSONObject(i).remove("windChill");
            }

            if(!(json.getJSONObject("siteData").get("currentConditions") instanceof String)){
                if(json.getJSONObject("siteData").getJSONObject("currentConditions").getJSONObject("pressure").get("change").toString().isEmpty()){
                    json.getJSONObject("siteData").getJSONObject("currentConditions").getJSONObject("pressure").put("change",0);
                }
            }


            jsString = json.toString().replaceAll("\\bclass\\b","clazz");
        }catch(Exception e){
            Log.e("ERROR: ", e.getMessage());
            Log.e("ERROR: ", e.getStackTrace().toString());
        }
        return jsString;
    }
}
