package com.example.meteocanada;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.example.meteocanada.Models.Weather;

import java.io.InputStream;

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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, villes);
        actv.setAdapter(adapter);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    String selectedItem = (String) parent.getItemAtPosition(position);
                    Log.i("SelectedItem", selectedItem);
                    Weather w  = WeatherDataParser.getWeatherDataByTownName(selectedItem);
                    setTown(w);
                }catch(Exception e){
                    Log.e("ERROR: ", e.getMessage());
                }
            }
        });
        loadData();
    }
    private void loadData(){
        ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(() ->{
            this.readDataFromFile("montreal_e");
            this.readDataFromFile("montreal_f");
            this.readDataFromFile("terrebonne_f");
            this.readDataFromFile("terrebonne_e");
            this.readDataFromFile("pincourt_e");
            this.readDataFromFile("pincourt_f");
            this.readDataFromFile("mirabel_e");
            this.readDataFromFile("mirabel_f");
            this.readDataFromFile("longueuil_e");
            this.readDataFromFile("longueuil_f");
            this.readDataFromFile("laval_e");
            this.readDataFromFile("laval_f");
            pd.dismiss();
            Weather w  = WeatherDataParser.getWeatherDataByTownName("montreal_e");
            setTown(w);
        }).start();
    }
    private void readDataFromFile(String fileName) {
        try{
            InputStream stream = getResources().openRawResource(getResources().getIdentifier(fileName,"raw",getActivity().getPackageName()));
            WeatherDataParser.getData(stream, fileName);
        }catch (Exception e){
            Log.e("ERROR: ", e.getMessage());
        }
    }
    private void setTown(Weather weather){
        TextView textView = getView().findViewById(R.id.test_location);
        textView.setText("");
        textView.setText(weather.city);


    }

}