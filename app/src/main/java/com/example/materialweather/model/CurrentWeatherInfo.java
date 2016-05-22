package com.example.materialweather.model;

/**
 * Created by GavinAndre on 2016/5/15.
 */
public class CurrentWeatherInfo {

    private String city;
    private String cityId;
    private String curTemp;
    private String curPM;
    private String weather;
    private String sunRise;
    private String sunSet;
    private String curWindDirection;
    private String curWindStrength;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCurTemp() {
        return curTemp;
    }

    public void setCurTemp(String curTemp) {
        this.curTemp = curTemp;
    }

    public String getCurPM() {
        return curPM;
    }

    public void setCurPM(String curPM) {
        this.curPM = curPM;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getSunRise() {
        return sunRise;
    }

    public void setSunRise(String sunRise) {
        this.sunRise = sunRise;
    }

    public String getSunSet() {
        return sunSet;
    }

    public void setSunSet(String sunSet) {
        this.sunSet = sunSet;
    }

    public String getCurWindDirection() {
        return curWindDirection;
    }

    public void setCurWindDirection(String curWindDirection) {
        this.curWindDirection = curWindDirection;
    }

    public String getCurWindStrength() {
        return curWindStrength;
    }

    public void setCurWindStrength(String curWindStrength) {
        this.curWindStrength = curWindStrength;
    }

}
