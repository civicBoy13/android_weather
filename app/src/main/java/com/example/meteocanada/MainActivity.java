package com.example.meteocanada;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.meteocanada.Models.CityInfo;
import com.example.meteocanada.Models.Weather;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Pour permettre la navigation entre les pages en utilisant des fragments
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.accueil_frag);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new Accueil()).commit();
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                switch (item.getItemId()){
                    case R.id.accueil_frag:
                        fragment = new Accueil();
                        break;
                    case R.id.a_propos_frag:
                        fragment = new A_Propos();
                        break;
                    case R.id.aide_frag:
                        fragment = new Aide();
                        break;
                    case R.id.debug_frag:
                            fragment = new debug();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container, fragment).commit();
                return true;
            }
        });
        SQLiteDatabase mydatabase = openOrCreateDatabase("your database name",MODE_PRIVATE,null);
        new Thread(()->{
            DBHelper helper = new DBHelper(getApplicationContext());
            //get en
            InputStream streamEn = NetworkClient.get("https://dd.weather.gc.ca/citypage_weather/docs/site_list_en.csv");
            ArrayList<CityInfo> infoEn = CityInfoParser.parseData(streamEn);
            for (CityInfo cityInfoEn: infoEn ) {
                helper.insertCityInfo(cityInfoEn, DBHelper.TBL_CITY_REPO_EN);
            }
            //get fr
            InputStream streamFr = NetworkClient.get("https://dd.weather.gc.ca/citypage_weather/docs/site_list_fr.csv");
            ArrayList<CityInfo> infoFr = CityInfoParser.parseData(streamFr);
            for (CityInfo cityInfoFr: infoFr ) {
                helper.insertCityInfo(cityInfoFr, DBHelper.TBL_CITY_REPO_FR);
            }

            CityInfo cityFr = helper.getCityByName(DBHelper.TBL_CITY_REPO_FR, "montreal");
            CityInfo cityEn = helper.getCityByName(DBHelper.TBL_CITY_REPO_EN, "montreal");
            Log.i("CITY FR:",cityFr.city + " " + cityFr.province + " " + cityFr.file_name + " " + String.valueOf(cityFr.lat) + " " + String.valueOf(cityFr.lon));
            Log.i("CITY EN:",cityEn.city + " " + cityEn.province + " " + cityEn.file_name + " " + String.valueOf(cityEn.lat) + " " + String.valueOf(cityEn.lon));

        }).start();
    }



}