package com.example.meteocanada.Models;

import java.util.ArrayList;

public class CurrentConditions {
    public Area station;
    public ArrayList<DateTime> dateTime;
    public String condition;
    public Temperature temperature;
    public IconCode iconCode;
    public DewPoint dewPoint;
    public WindChill windChill;
    public Pressure pressure;
    public Visibility visibility;
    public RelativeHumidity relativeHumidity;
    public Wind wind;
}
