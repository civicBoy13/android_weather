package com.example.meteocanada;

import android.util.Log;

import com.example.meteocanada.Models.CityInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Scanner;


public class CityInfoParser {
    private static ArrayList<CityInfo> cityInfos = new ArrayList<>();

    public static ArrayList<CityInfo> parseData(InputStream stream){
//        debug(stream);
        cityInfos = new ArrayList<>();
        try(Scanner input = new Scanner(stream).useDelimiter(",|\\n")) {
            if(input.hasNext()){
                input.next();
                input.next();
                input.next();
                input.next();
                input.next();
                input.next();
                input.next();
                input.next();
                input.next();
                input.next();
            }

            while(input.hasNext()){
                CityInfo info = new CityInfo();
                 info.setFile_name(input.next());
                info.setCity(input.next());
                info.setProvince(input.next());
                info.setLat(input.next());
                info.setLon(input.next());
                cityInfos.add(info);
            }
        }catch (Exception e){
            Log.e("ERROR:", e.getMessage());
        }
        return cityInfos;
    }

    private static void debug(InputStream stream){
        try {
            int n = 0;
            char[] buffer = new char[1024 * 4];
            InputStreamReader reader = new InputStreamReader(stream, "UTF8");
            StringWriter writer = new StringWriter();
            while (-1 != (n = reader.read(buffer))) writer.write(buffer, 0, n);
            Log.i("DEBUG:" , writer.toString());
        }catch (Exception e){

        }
    }
}
