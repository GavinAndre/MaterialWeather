package com.example.materialweather.util;

import android.text.TextUtils;
import android.util.Log;


import com.example.materialweather.R;
import com.example.materialweather.db.MaterialWeatherDB;
import com.example.materialweather.model.City;
import com.example.materialweather.model.CityManage;
import com.example.materialweather.model.County;
import com.example.materialweather.model.CurrentWeatherInfo;
import com.example.materialweather.model.Province;
import com.example.materialweather.model.RecentWeatherInfo;
import com.example.materialweather.model.WeatherIndexes;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GavinAndre on 2015/11/16 0016.
 */
public class Utility {

    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(
            MaterialWeatherDB materialWeatherDB, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //将解析出来的数据存储到Province表
                    materialWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public static boolean handleCitiesResponse(MaterialWeatherDB materialWeatherDB
            , String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    //将解析出来的数据存储到City表
                    materialWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public static boolean handleCountiesResponse(MaterialWeatherDB materialWeatherDB
            , String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String[] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for (String c : allCounties) {
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    //将解析出来的数据存储到County表
                    materialWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }

    public static boolean handleCodeResponse(MaterialWeatherDB materialWeatherDB
            , String response, String name) {
        if (!TextUtils.isEmpty(response)) {
            //从服务器返回的数据中解析出天气代号
            String[] array = response.split("\\|");
            if (array != null && array.length == 2) {
                CityManage cityManage = new CityManage();
                cityManage.setName(name);
                cityManage.setCode(array[1]);
                Log.e("CityManage", cityManage.getName());
                Log.e("CityManage", cityManage.getCode());
                materialWeatherDB.saveCityManage(cityManage);
                return true;
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的JSON数据,并将解析出的数据存储到本地
     */
    public static void handleCurrentWeatherResponse(MaterialWeatherDB materialWeatherDB, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject retData = jsonObject.getJSONObject("retData");
            String cityName = retData.getString("city");//城市
            String cityCode = retData.getString("citycode");//城市编码
            String temp = retData.getString("temp");//当前温度
            String tempHigh = retData.getString("l_tmp");//最低温度
            String tempLow = retData.getString("h_tmp");//最高温度
            String weather = retData.getString("weather");//天气情况
            String publishTime = retData.getString("time");//发布时间
            String windDirection = retData.getString("WD");//风向
            String windStrength = retData.getString("WS");//风力
            String sunRise = retData.getString("sunrise");//日出时间
            String sunSet = retData.getString("sunset");//日落时间
            saveCurrentWeatherInfo(materialWeatherDB, cityName, cityCode, temp, tempHigh,
                    tempLow, weather, publishTime, windDirection,
                    windStrength, sunRise, sunSet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleRecentWeathersResponse(MaterialWeatherDB materialWeatherDB, String Response) {
        try {

            ArrayList<String> strArrayDate = new ArrayList<>();
            ArrayList<String> strArrayWeek = new ArrayList<>();
            ArrayList<String> strArrayWindStrength = new ArrayList<>();
            ArrayList<String> strArrayWindDirection = new ArrayList<>();
            ArrayList<String> strArrayHighTemp = new ArrayList<>();
            ArrayList<String> strArrayLowTemp = new ArrayList<>();
            ArrayList<String> strArrayType = new ArrayList<>();

            ArrayList<String> strArrayName = new ArrayList<>();
            ArrayList<String> strArrayCode = new ArrayList<>();
            ArrayList<String> strArrayIndex = new ArrayList<>();
            ArrayList<String> strArrayDetails = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(Response);
            JSONObject retData = jsonObject.getJSONObject("retData");
            String city = retData.getString("city");
            String cityId = retData.getString("cityid");
            JSONObject today = retData.getJSONObject("today");
            String curTemp = today.getString("curTemp");
            String curPM = today.getString("aqi");
            String curDate = today.getString("date");
            String curWeek = today.getString("week");
            String curWindDirection = today.getString("fengxiang");
            String curWindStrength = today.getString("fengli");
            String curHighTemp = today.getString("hightemp");
            String curLowTemp = today.getString("lowtemp");
            String curType = today.getString("type");


            JSONArray todayIndexArray = today.getJSONArray("index");
            for (int i = 0; i < todayIndexArray.length(); i++) {
                JSONObject todayIndexObject = todayIndexArray.getJSONObject(i);
                String name = todayIndexObject.getString("name");
                String code = todayIndexObject.getString("code");
                String index = todayIndexObject.getString("index");
                String details = todayIndexObject.getString("details");

                strArrayName.add(name);
                strArrayCode.add(code);
                strArrayIndex.add(index);
                strArrayDetails.add(details);
            }

            JSONArray historyArray = retData.getJSONArray("history");
            JSONObject historyObject = historyArray.getJSONObject(historyArray.length() - 1);
            String historyDate = historyObject.getString("date");
            String historyWeek = historyObject.getString("week");
            String historyWindDirection = historyObject.getString("fengxiang");
            String historyWindStrength = historyObject.getString("fengli");
            String historyHighTemp = historyObject.getString("hightemp");
            String historyLowTemp = historyObject.getString("lowtemp");
            String historyType = historyObject.getString("type");

            strArrayDate.add(0, historyDate);
            strArrayWeek.add(0, historyWeek);
            strArrayWindDirection.add(0, historyWindDirection);
            strArrayWindStrength.add(0, historyWindStrength);
            strArrayHighTemp.add(0, historyHighTemp);
            strArrayLowTemp.add(0, historyLowTemp);
            strArrayType.add(0, historyType);

            strArrayDate.add(1, curDate);
            strArrayWeek.add(1, curWeek);
            strArrayWindDirection.add(1, curWindDirection);
            strArrayWindStrength.add(1, curWindStrength);
            strArrayHighTemp.add(1, curHighTemp);
            strArrayLowTemp.add(1, curLowTemp);
            strArrayType.add(1, curType);

            JSONArray forecastArray = retData.getJSONArray("forecast");
            for (int i = 0; i < forecastArray.length(); i++) {
                JSONObject forecastObject = forecastArray.getJSONObject(i);
                String forecastDate = forecastObject.getString("date");
                String forecastWeek = forecastObject.getString("week");
                String forecastWindDirection = forecastObject.getString("fengxiang");
                String forecastWindStrength = forecastObject.getString("fengli");
                String forecastHighTemp = forecastObject.getString("hightemp");
                String forecastLowTemp = forecastObject.getString("lowtemp");
                String forecastType = forecastObject.getString("type");

                strArrayDate.add(2 + i, forecastDate);
                strArrayWeek.add(2 + i, forecastWeek);
                strArrayWindDirection.add(2 + i, forecastWindDirection);
                strArrayWindStrength.add(2 + i, forecastWindStrength);
                strArrayHighTemp.add(2 + i, forecastHighTemp);
                strArrayLowTemp.add(2 + i, forecastLowTemp);
                strArrayType.add(2 + i, forecastType);
            }

            saveRecentWeatherInfo(materialWeatherDB, city, cityId, curTemp, curPM, strArrayDate,
                    strArrayWeek, strArrayWindStrength, strArrayWindDirection, strArrayHighTemp,
                    strArrayLowTemp, strArrayType, strArrayName, strArrayCode,
                    strArrayIndex, strArrayDetails);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences文件中
     */
    public static void saveCurrentWeatherInfo(MaterialWeatherDB materialWeatherDB, String cityName
            , String cityCode, String temp, String tempHigh, String tempLow
            , String weather, String publishTime, String windDirection
            , String windStrength, String sunRise, String sunSet) {

        CurrentWeatherInfo currentWeatherInfo = new CurrentWeatherInfo();
        currentWeatherInfo.setWeather(weather);
        currentWeatherInfo.setSunRise(sunRise);
        currentWeatherInfo.setSunSet(sunSet);
        currentWeatherInfo.setCurWindDirection(windDirection);
        currentWeatherInfo.setCurWindStrength(windStrength);
        materialWeatherDB.updateCurrentWeatherInfo(currentWeatherInfo, cityCode);
    }

    public static void saveRecentWeatherInfo(MaterialWeatherDB materialWeatherDB, String city
            , String cityId, String curTemp, String curPM
            , ArrayList<String> strArrayDate, ArrayList<String> strArrayWeek
            , ArrayList<String> strArrayWindStrength, ArrayList<String> strArrayWindDirection
            , ArrayList<String> strArrayHighTemp, ArrayList<String> strArrayLowTemp
            , ArrayList<String> strArrayType, ArrayList<String> strArrayName
            , ArrayList<String> strArrayCode, ArrayList<String> strArrayIndex
            , ArrayList<String> strArrayDetails) {

        CurrentWeatherInfo currentWeatherInfo = new CurrentWeatherInfo();
        currentWeatherInfo.setCity(city);
        currentWeatherInfo.setCityId(cityId);
        currentWeatherInfo.setCurTemp(curTemp);
        currentWeatherInfo.setCurPM(curPM);
        materialWeatherDB.saveCurrentWeatherInfo(currentWeatherInfo);

        for (int i = 0; i < strArrayDate.size(); i++) {
            RecentWeatherInfo recentWeatherInfo = new RecentWeatherInfo();
            recentWeatherInfo.setDate(strArrayDate.get(i));
            recentWeatherInfo.setWeek(strArrayWeek.get(i));
            recentWeatherInfo.setWindStrength(strArrayWindStrength.get(i));
            recentWeatherInfo.setWindDirection(strArrayWindDirection.get(i));
            recentWeatherInfo.setHighTemp(strArrayHighTemp.get(i));
            recentWeatherInfo.setLowTemp(strArrayLowTemp.get(i));
            recentWeatherInfo.setType(strArrayType.get(i));
            recentWeatherInfo.setCityId(cityId);
            materialWeatherDB.saveRecentWeatherInfo(recentWeatherInfo, i);
        }

        for (int i = 0; i < strArrayName.size(); i++) {
            WeatherIndexes weatherIndexes = new WeatherIndexes();
            weatherIndexes.setName(strArrayName.get(i));
            weatherIndexes.setCode(strArrayCode.get(i));
            weatherIndexes.setIndex(strArrayIndex.get(i));
            weatherIndexes.setDetails(strArrayDetails.get(i));
            weatherIndexes.setCityId(cityId);
            materialWeatherDB.saveWeatherIndexes(weatherIndexes, i);
        }

    }

    public static Map<String,Integer> weatherIconMap = getWeatherIconMap();
    private static Map<String,Integer> getWeatherIconMap(){
        Map<String,Integer> map = new HashMap<>();
        map.put("晴", R.mipmap.w00);
        map.put("多云",R.mipmap.w01);
        map.put("阴",R.mipmap.w02);
        map.put("阵雨",R.mipmap.w03);
        map.put("雷阵雨",R.mipmap.w04);
        map.put("雷阵雨伴有冰雹",R.mipmap.w05);
        map.put("雨夹雪",R.mipmap.w06);
        map.put("小雨",R.mipmap.w07);
        map.put("中雨",R.mipmap.w08);
        map.put("大雨",R.mipmap.w09);
        map.put("暴雨",R.mipmap.w10);
        map.put("大暴雨",R.mipmap.w11);
        map.put("特大暴雨",R.mipmap.w12);
        map.put("阵雪",R.mipmap.w13);
        map.put("小雪",R.mipmap.w14);
        map.put("中雪",R.mipmap.w15);
        map.put("大雪",R.mipmap.w16);
        map.put("暴雪",R.mipmap.w17);
        map.put("雾",R.mipmap.w18);
        map.put("冻雨",R.mipmap.w19);
        map.put("沙尘暴",R.mipmap.w20);
        map.put("小到中雨",R.mipmap.w21);
        map.put("中到大雨",R.mipmap.w22);
        map.put("大到暴雨",R.mipmap.w23);
        map.put("暴雨到大暴雨",R.mipmap.w24);
        map.put("大暴雨到特大暴雨",R.mipmap.w25);
        map.put("小到中雪",R.mipmap.w26);
        map.put("中到大雪",R.mipmap.w27);
        map.put("大到暴雪",R.mipmap.w28);
        map.put("浮尘",R.mipmap.w29);
        map.put("扬沙",R.mipmap.w30);
        map.put("强沙尘暴",R.mipmap.w31);
        map.put("霾",R.mipmap.w53);
        map.put("无",R.mipmap.w99);
        return map;
    }

    public static Map<String,Integer> suggestionIconMap = getSuggestionIconMap();
    private static Map<String,Integer> getSuggestionIconMap(){
        Map<String,Integer> map = new HashMap<>();
        map.put("感冒指数",R.mipmap.red);
        map.put("防晒指数",R.mipmap.sun_screen);
        map.put("穿衣指数",R.mipmap.clothes);
        map.put("运动指数",R.mipmap.foot_ball);
        map.put("洗车指数",R.mipmap.car);
        map.put("晾晒指数",R.mipmap.hanger);

        return map;
    }

}
