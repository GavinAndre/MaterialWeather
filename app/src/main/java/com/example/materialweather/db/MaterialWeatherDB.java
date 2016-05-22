package com.example.materialweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.materialweather.model.City;
import com.example.materialweather.model.CityManage;
import com.example.materialweather.model.County;
import com.example.materialweather.model.CurrentWeatherInfo;
import com.example.materialweather.model.Province;
import com.example.materialweather.model.RecentWeatherInfo;
import com.example.materialweather.model.WeatherIndexes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GavinAndre on 2015/11/16 0016.
 */
public class MaterialWeatherDB {

    /**
     * 数据库名
     */
    public static final String DB_NAME = "cool_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static MaterialWeatherDB sMaterialWeatherDB;

    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private MaterialWeatherDB(Context context) {
        MaterialWeatherOpenHelper dbHelper = new MaterialWeatherOpenHelper(context,
                DB_NAME, null, VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取MaterialWeatherDB
     */
    public synchronized static MaterialWeatherDB getInstance(Context context) {
        if (sMaterialWeatherDB == null) {
            sMaterialWeatherDB = new MaterialWeatherDB(context);
        }
        return sMaterialWeatherDB;
    }

    /**
     * 将Province实例存储到数据库
     */
    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    /**
     * 从数据库读取全国所有的省份信息
     */
    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query(
                "Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor
                        .getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor
                        .getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 将City实例存储到数据库
     */
    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_name", city.getCityName());
            values.put("city_code", city.getCityCode());
            values.put("province_id", city.getProvinceId());
            db.insert("City", null, values);
        }
    }

    /**
     * 从数据库读取某省下所有的城市信息
     */
    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("City", null, "province_id=?",
                new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor
                        .getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor
                        .getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    /**
     * 将County实例存储到数据库
     */
    public void saveCounty(County county) {
        if (county != null) {
            ContentValues values = new ContentValues();
            values.put("county_name", county.getCountyName());
            values.put("county_code", county.getCountyCode());
            values.put("city_id", county.getCityId());
            db.insert("County", null, values);
        }
    }

    /**
     * 从数据库读取某城市下所有的县信息
     */
    public List<County> loadCounties(int cityId) {
        List<County> list = new ArrayList<>();
        Cursor cursor = db.query("County", null, "city_id=?",
                new String[]{String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setid(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor
                        .getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor
                        .getColumnIndex("county_code")));
                county.setCityId(cityId);
                list.add(county);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public void saveCityManage(CityManage cityManage) {
        if (cityManage != null) {
            ContentValues values = new ContentValues();
            values.put("name", cityManage.getName());
            values.put("code", cityManage.getCode());
            Log.e("saveCityManage", cityManage.getName());
            Log.e("saveCityManage", cityManage.getCode());
            Cursor cursor = db.query("CityManage", null, "code=?",
                    new String[]{cityManage.getCode()}, null, null, null);
            if (cursor.getCount() == 0) {
                db.insert("CityManage", null, values);
            }
            cursor.close();
        }
    }

    public List<CityManage> loadCityManage() {
        List<CityManage> list = new ArrayList<>();
        Cursor cursor = db.query("CityManage", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CityManage cityManage = new CityManage();
                cityManage.setId(cursor.getColumnIndex("id"));
                cityManage.setName(cursor.getString(cursor
                        .getColumnIndex("name")));
                cityManage.setCode(cursor.getString(cursor
                        .getColumnIndex("code")));
                list.add(cityManage);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public void deleteCityManage(String cityId) {
        Cursor cursor = db.query("CityManage", null, "code=?",
                new String[]{cityId}, null, null, null);
        if (cursor.getCount() != 0) {
            db.delete("CityManage", "code=?", new String[]{cityId});
        }
        cursor.close();
    }


    public void saveCurrentWeatherInfo(CurrentWeatherInfo currentWeatherInfo) {
        if (currentWeatherInfo != null) {
            ContentValues values = new ContentValues();
            values.put("city", currentWeatherInfo.getCity());
            values.put("cityId", currentWeatherInfo.getCityId());
            values.put("curTemp", currentWeatherInfo.getCurTemp());
            values.put("curPM", currentWeatherInfo.getCurPM());
            Cursor cursor = db.query("CurrentWeatherInfo", null, "cityId=?",
                    new String[]{currentWeatherInfo.getCityId()}, null, null, null);
            if (cursor.getCount() == 0) {
                db.insert("CurrentWeatherInfo", null, values);
            } else {
                db.update("CurrentWeatherInfo", values, "cityId=?", new String[]{
                        currentWeatherInfo.getCityId()});
            }
            cursor.close();
        }
    }

    public void updateCurrentWeatherInfo(CurrentWeatherInfo currentWeatherInfo, String cityId) {
        if (currentWeatherInfo != null) {
            ContentValues values = new ContentValues();
            values.put("weather", currentWeatherInfo.getWeather());
            values.put("sunRise", currentWeatherInfo.getSunRise());
            values.put("sunSet", currentWeatherInfo.getSunSet());
            values.put("curWindDirection", currentWeatherInfo.getCurWindDirection());
            values.put("curWindStrength", currentWeatherInfo.getCurWindStrength());
            db.update("CurrentWeatherInfo", values, "cityId=?", new String[]{cityId});
        }
    }

    public List<CurrentWeatherInfo> loadCurrentWeatherInfo(String cityId) {
        List<CurrentWeatherInfo> list = new ArrayList<>();
        Cursor cursor = db.query("CurrentWeatherInfo", null, "cityId=?",
                new String[]{cityId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CurrentWeatherInfo currentWeatherInfo = new CurrentWeatherInfo();
                currentWeatherInfo.setCity(cursor.getString(cursor.getColumnIndex("city")));
                currentWeatherInfo.setCityId(cityId);
                currentWeatherInfo.setCurTemp(cursor.getString(cursor.getColumnIndex("curTemp")));
                currentWeatherInfo.setCurPM(cursor.getString(cursor.getColumnIndex("curPM")));
                currentWeatherInfo.setWeather(cursor.getString(cursor.getColumnIndex("weather")));
                currentWeatherInfo.setSunRise(cursor.getString(cursor.getColumnIndex("sunRise")));
                currentWeatherInfo.setSunSet(cursor.getString(cursor.getColumnIndex("sunSet")));
                currentWeatherInfo.setCurWindDirection(cursor.getString(cursor.getColumnIndex("curWindDirection")));
                currentWeatherInfo.setCurWindStrength(cursor.getString(cursor.getColumnIndex("curWindStrength")));
                list.add(currentWeatherInfo);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public void saveRecentWeatherInfo(RecentWeatherInfo recentWeatherInfo, int i) {
        if (recentWeatherInfo != null) {
            String week = recentWeatherInfo.getWeek();
            week = week.substring(2, week.length());
            String highTemp = recentWeatherInfo.getHighTemp();
            highTemp = highTemp.substring(0, highTemp.length() - 1);
            String lowTemp = recentWeatherInfo.getLowTemp();
            lowTemp = lowTemp.substring(0, lowTemp.length() - 1);
            ContentValues values = new ContentValues();
            values.put("date", recentWeatherInfo.getDate());
            values.put("week", week);
            values.put("windDirection", recentWeatherInfo.getWindDirection());
            values.put("windStrength", recentWeatherInfo.getWindStrength());
            values.put("highTemp", highTemp);
            values.put("lowTemp", lowTemp);
            values.put("type", recentWeatherInfo.getType());
            values.put("cityId", recentWeatherInfo.getCityId());
            Cursor cursor = db.query("RecentWeatherInfo", null, "cityId=?",
                    new String[]{recentWeatherInfo.getCityId()}, null, null, null);
            if (cursor.getCount() < 6) {
                db.insert("RecentWeatherInfo", null, values);
            } else {
                cursor.moveToPosition(i);
                db.update("RecentWeatherInfo", values, "id=?", new String[]{
                        cursor.getString(cursor.getColumnIndex("id"))});
            }
            cursor.close();
        }
    }

    public List<RecentWeatherInfo> loadRecentWeatherInfo(String cityId) {
        List<RecentWeatherInfo> list = new ArrayList<>();
        Cursor cursor = db.query("RecentWeatherInfo", null, "cityId=?",
                new String[]{cityId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                RecentWeatherInfo recentWeatherInfo = new RecentWeatherInfo();
                recentWeatherInfo.setDate(cursor.getString(cursor.getColumnIndex("date")));
                recentWeatherInfo.setWeek(cursor.getString(cursor.getColumnIndex("week")));
                recentWeatherInfo.setWindDirection(cursor.getString(cursor.getColumnIndex("windDirection")));
                recentWeatherInfo.setWindStrength(cursor.getString(cursor.getColumnIndex("windStrength")));
                recentWeatherInfo.setHighTemp(cursor.getString(cursor.getColumnIndex("highTemp")));
                recentWeatherInfo.setLowTemp(cursor.getString(cursor.getColumnIndex("lowTemp")));
                recentWeatherInfo.setType(cursor.getString(cursor.getColumnIndex("type")));
                recentWeatherInfo.setCityId(cityId);
                list.add(recentWeatherInfo);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }

    public void saveWeatherIndexes(WeatherIndexes weatherIndexes, int i) {
        if (weatherIndexes != null) {
            ContentValues values = new ContentValues();
            values.put("name", weatherIndexes.getName());
            values.put("code", weatherIndexes.getCode());
            values.put("indexes", weatherIndexes.getIndex());
            values.put("details", weatherIndexes.getDetails());
            values.put("cityId", weatherIndexes.getCityId());
            Cursor cursor = db.query("WeatherIndexes", null, "cityId=?",
                    new String[]{weatherIndexes.getCityId()}, null, null, null);
            if (cursor.getCount() < 6) {
                db.insert("WeatherIndexes", null, values);
            } else {
                cursor.moveToPosition(i);
                db.update("WeatherIndexes", values, "id=?", new String[]{
                        cursor.getString(cursor.getColumnIndex("id"))});
            }
            cursor.close();
        }
    }

    public List<WeatherIndexes> loadWeatherIndexes(String cityId) {
        List<WeatherIndexes> list = new ArrayList<>();
        Cursor cursor = db.query("WeatherIndexes", null, "cityId=?",
                new String[]{cityId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                WeatherIndexes weatherIndexes = new WeatherIndexes();
                weatherIndexes.setName(cursor.getString(cursor.getColumnIndex("name")));
                weatherIndexes.setCode(cursor.getString(cursor.getColumnIndex("code")));
                weatherIndexes.setIndex(cursor.getString(cursor.getColumnIndex("indexes")));
                weatherIndexes.setDetails(cursor.getString(cursor.getColumnIndex("details")));
                weatherIndexes.setCityId(cityId);
                list.add(weatherIndexes);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }
        return list;
    }
}
