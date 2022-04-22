package com.example.meteocanada;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.meteocanada.Models.CityInfo;
import com.example.meteocanada.Models.Weather;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Accueil#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Accueil extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String[] villes;
    private ArrayAdapter<String> adapter = null;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Accueil() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Accueil.
     */
    // TODO: Rename and change types and number of parameters
    public static Accueil newInstance(String param1, String param2) {
        Accueil fragment = new Accueil();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_accueil, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        villes = getResources().getStringArray(R.array.villes);
        AutoCompleteTextView actv = getView().findViewById(R.id.rechercheVille);
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        actv.setAdapter(adapter);
        actv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try{
                    adapter.clear();
                    DBHelper helper = new DBHelper(getActivity().getApplicationContext());
                    String table = helper.TBL_CITY_REPO_EN;
                    if(Locale.getDefault().getLanguage() != "en"){
                        table = helper.TBL_CITY_REPO_FR;
                    }
                    ArrayList<String> res = helper.searchCity(table,charSequence.toString());
                    adapter.addAll(res);
                }catch (Exception e){
                    Log.e("Error", e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    String selectedItem = (String) parent.getItemAtPosition(position);

                    ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage("Searching");
                    pd.setCanceledOnTouchOutside(false);
                    pd.show();
                    new Thread(() -> {
                        Weather weather = null;
                        for (Weather w: WeatherDataParser.weathers) {
                            if(w.city == selectedItem){
                                weather = w;
                                break;
                            }
                        }

                        if(weather == null){
                            DBHelper helper = new DBHelper(getActivity().getApplicationContext());

                            String table = helper.TBL_CITY_REPO_EN;
                            String lang = "_e";

                            if(Locale.getDefault().getLanguage() != "en"){
                                table = helper.TBL_CITY_REPO_FR;
                                lang = "_f";
                            }

                            CityInfo info = helper.getCityByName(table,selectedItem);

                            String url = "https://dd.weather.gc.ca/citypage_weather/xml/"+info.province+"/"+info.file_name+lang+".xml";
                            InputStream stream= NetworkClient.get(url);
                            weather = WeatherDataParser.getData(stream,info.city);

                        }
                        pd.dismiss();
                        setValues(weather);
                    }).start();
                }catch(Exception e){
                    Log.e("ERROR: ", e.getMessage());
                }
            }
        });
        loadData();
    }
    private void loadData(){
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Getting info from server");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(() ->{
            DBHelper helper = new DBHelper(getActivity().getApplicationContext());
            //get en
            int countEN = helper.getTableCount("CityRepoEN");

            if( countEN < 849){
                updateLoadingMessage(pd,"Getting english city info");

                InputStream streamEn = NetworkClient.get("https://dd.weather.gc.ca/citypage_weather/docs/site_list_en.csv");
                ArrayList<CityInfo> infoEn = CityInfoParser.parseData(streamEn);
                Log.i("count", String.valueOf(infoEn.size()));


                for(int i = 0; i < infoEn.size(); i ++){
                    CityInfo cityInfoEn = infoEn.get(i);
                    int percent = (100 * i) /infoEn.size();
                    updateLoadingMessage(pd,"Saving "+ cityInfoEn.city + " info. Status " + String.valueOf(percent) + " %");
                    helper.insertCityInfo(cityInfoEn, DBHelper.TBL_CITY_REPO_EN);
                }

            }

            //get fr
            int countFR = helper.getTableCount("CityRepoFR");
            if( countFR < 849){
                updateLoadingMessage(pd,"Getting french city info");
                InputStream streamFr = NetworkClient.get("https://dd.weather.gc.ca/citypage_weather/docs/site_list_fr.csv");
                ArrayList<CityInfo> infoFr = CityInfoParser.parseData(streamFr);
                for(int j = 0; j < infoFr.size(); j ++){
                    CityInfo cityInfoFR = infoFr.get(j);
                    int percent = (100 * j) /infoFr.size();
                    updateLoadingMessage(pd,"Saving "+ cityInfoFR.city + " info. Status " + String.valueOf(percent) + " %");
                    helper.insertCityInfo(cityInfoFR, DBHelper.TBL_CITY_REPO_FR);
                }

            }
            pd.dismiss();
        }).start();
    }

    private void updateLoadingMessage(ProgressDialog pd, String message){
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                pd.setMessage(message);
            }
        });
    }

    private void readDataFromFile(String fileName) {
        try{
            InputStream stream = getResources().openRawResource(getResources().getIdentifier(fileName,"raw",getActivity().getPackageName()));
            WeatherDataParser.getData(stream, fileName);
        }catch (Exception e){
            Log.e("ERROR: ", e.getMessage());
        }
    }
    private void setValues(Weather weather){
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                TextView city =  getView().findViewById(R.id.city_value);
                TextView temp =  getView().findViewById(R.id.temp_value);
                city.setText(weather.city);
                temp.setText(String.valueOf(weather.siteData.currentConditions.temperature.content) + " C");
            }
        });


    }

}