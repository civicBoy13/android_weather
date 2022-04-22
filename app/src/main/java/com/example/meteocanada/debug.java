package com.example.meteocanada;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link debug#newInstance} factory method to
 * create an instance of this fragment.
 */
public class debug extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public debug() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment debug.
     */
    // TODO: Rename and change types and number of parameters
    public static debug newInstance(String param1, String param2) {
        debug fragment = new debug();
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
    public void onResume() {
        super.onResume();
        new Thread(() -> {
            this.setData();
        }).start();

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_debug, container, false);
    }

    protected void setData(){
        InputStream stream = NetworkClient.get("https://dd.weather.gc.ca/citypage_weather/xml/QC/s0000027_f.xml");
        String output = WeatherDataParser.getData(stream);
        setText(output);
    }

    private void setText(String text){
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = getView().findViewById(R.id.debug_text_view);
                tv.setText("");
                tv.setText(text);
            }
        });
    }
}