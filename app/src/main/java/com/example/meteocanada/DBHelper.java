package com.example.meteocanada;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.meteocanada.Models.CityInfo;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Weather.db";

    //col names
    public static final String FILE_NAME = "file_name";
    public static final String CITY = "city";
    public static final String PROVINCE = "province";
    public static final String LAT = "lat";
    public static final String LON = "lon";

    public static String TBL_CITY_REPO_EN = "CityRepoEn";
    public static String TBL_CITY_REPO_FR = "CityRepoFr";

    public static String CREATE_TBL_CITY_REPO_EN = "CREATE TABLE IF NOT EXISTS " + TBL_CITY_REPO_EN + "(" +
                                                    "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                                    "file_name TEXT NOT NULL,"+
                                                    "city TEXT NOT NULL,"+
                                                    "province TEXT NOT NULL,"+
                                                    "lat REAL NOT NULL,"+
                                                    "lon REAL NOT NULL, "+
                                                    "UNIQUE(city)); ";
    public static String CREATE_TBL_CITY_REPO_FR = "CREATE TABLE IF NOT EXISTS " + TBL_CITY_REPO_FR + "("+
                                                    "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                                                    "file_name TEXT NOT NULL,"+
                                                    "city TEXT NOT NULL,"+
                                                    "province TEXT NOT NULL,"+
                                                    "lat REAL,"+
                                                    "lon REAL, "+
                                                    "UNIQUE(city)); ";


    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TBL_CITY_REPO_EN);
        db.execSQL(CREATE_TBL_CITY_REPO_FR);
    }


    public ArrayList<String> searchCity(String table,String term){
        ArrayList<String> results = new ArrayList<>();
        try{
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE city LIKE '%"+term+"%'" ,null);
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do{
                    int index = cursor.getColumnIndex("city");
                    results.add(cursor.getString(index));
                }while (cursor.moveToNext());

            }
            cursor.close();
            db.close();
        }catch (Exception e){
            Log.e("ERROR:", e.getMessage());
        }
        return results;
    }
    public CityInfo getCityByName(String table,String name){
        CityInfo city = null;
        try{
            city = new CityInfo();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE city LIKE '%"+name+"%'" ,null);
            if (!cursor.moveToFirst())
                cursor.moveToFirst();

            int file_name_index = cursor.getColumnIndexOrThrow("file_name");
            city.file_name = cursor.getString(file_name_index);

            int city_index = cursor.getColumnIndexOrThrow("city");
            city.city = cursor.getString(city_index);

            int province_index = cursor.getColumnIndexOrThrow("province");
            city.province = cursor.getString(province_index);

            int lat_index = cursor.getColumnIndexOrThrow("lat");
            city.lat = cursor.getString(lat_index);

            int lon_index = cursor.getColumnIndexOrThrow("lon");
            city.lon = cursor.getString(lon_index);
            cursor.close();
            db.close();
        }catch (Exception e){
            Log.e("ERROR:", e.getMessage());
        }
        return city;
    }

    public int getTableCount(String tableName){
        int count = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + tableName + " ;" ,null);
            if (!cursor.moveToFirst())
                cursor.moveToFirst();
            int index = cursor.getColumnIndexOrThrow("COUNT(*)");
            count = cursor.getInt(index);
            cursor.close();
            db.close();
        }catch (Exception e){
            Log.e("ERROR:", e.getMessage());
        }
        return  count;
    }

    public CityInfo getCityByProvince(String table,String province){
        CityInfo city = new CityInfo();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + table + " WHERE province LIKE '%"+province+"%'" ,null);

        int file_name_index = cursor.getColumnIndexOrThrow("file_name");
        city.file_name = cursor.getString(file_name_index);

        int city_index = cursor.getColumnIndexOrThrow("city");
        city.city = cursor.getString(city_index);

        int province_index = cursor.getColumnIndexOrThrow("province");
        city.province = cursor.getString(province_index);

        int lat_index = cursor.getColumnIndexOrThrow("lat");
        city.lat = cursor.getString(lat_index);

        int lon_index = cursor.getColumnIndexOrThrow("lon");
        city.lon = cursor.getString(lon_index);
        cursor.close();
        db.close();
        return city;
    }

    public void insertCityInfo(CityInfo city, String type){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("INSERT or REPLACE into " + type +" (file_name, city, province, lat, lon) VALUES ( '"+ city.file_name+ "', '" + city.city.replaceAll("'","''") +"', '"+city.province+"', '"+city.lat + "','"+city.lon+"')");
        }catch (Exception e){
            Log.e("ERROR:", e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TBL_CITY_REPO_EN);
        db.execSQL("DROP TABLE IF EXISTS " + TBL_CITY_REPO_FR);
        onCreate(db);
    }
}
