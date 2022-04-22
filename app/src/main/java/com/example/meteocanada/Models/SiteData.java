package com.example.meteocanada.Models;

import java.util.ArrayList;

public class SiteData {
    public ArrayList<DateTime> dateTime;
    public Location location;
    public String warning;
    public CurrentConditions currentConditions;
    public ForecastGroup forecastGroup;
    public HourlyForecastGroup hourlyForecastGroup;
    public YesterdayConditions yesterdayConditions;
    public RiseSet riseSet;
    public Almanac almanac;
}
