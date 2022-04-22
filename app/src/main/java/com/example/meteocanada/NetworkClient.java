package com.example.meteocanada;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class NetworkClient {
    public static InputStream get(String url){
        InputStream data = null;
        try{
            URL urlObj = new URL(url);
            URLConnection connection = urlObj.openConnection();
            if (!(connection instanceof HttpURLConnection)) {
                throw new IOException("URL is not an Http URL");
            }

            HttpURLConnection httpConn = (HttpURLConnection)connection;
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            int statusCode = httpConn.getResponseCode();

            if(statusCode == HttpURLConnection.HTTP_OK){
                data = httpConn.getInputStream();
            }

        }catch (Exception e){
            Log.e("ERROR:", e.getMessage());
        }
        return data;
    }
}