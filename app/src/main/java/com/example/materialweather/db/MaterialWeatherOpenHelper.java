package com.example.materialweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by GavinAndre on 2015/11/16 0016.
 */
public class MaterialWeatherOpenHelper extends SQLiteOpenHelper {

    /**
     * Province表建表语句
     */
    public static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement, "
            + "province_name text,"
            + "province_code text)";

    /**
     * City表建表语句
     */
    public static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement, "
            + "city_name text, "
            + "city_code text, "
            + "province_id integer)";

    /**
     * County表建表语句
     */
    public static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement, "
            + "county_name text, "
            + "county_code text, "
            + "city_id integer)";


    public static final String CREATE_CURWEATHERINFO = "create table CurrentWeatherInfo("
            + "id integer primary key autoincrement, "
            + "city text,"
            + "cityId text,"
            + "curTemp text,"
            + "curPM text,"
            + "weather text,"
            + "sunRise text,"
            + "sunSet text,"
            + "curWindStrength text,"
            + "curWindDirection text)";

    public static final String CREATE_RECENTWEATHERINFO = "create table RecentWeatherInfo("
            + "id integer primary key autoincrement, "
            + "date text,"
            + "week text,"
            + "windDirection text,"
            + "windStrength text,"
            + "highTemp text,"
            + "lowTemp text,"
            + "type text,"
            + "cityId text)";

    public static final String CREATE_WEATHERINDEXES = "create table WeatherIndexes("
            + "id integer primary key autoincrement, "
            + "name text,"
            + "code text,"
            + "indexes text,"
            + "details text,"
            + "cityId text)";

    public static final String CREATE_CITYMANAGE = "create table CityManage ("
            + "id integer primary key autoincrement, "
            + "name text, "
            + "code text)";

    public MaterialWeatherOpenHelper(Context context, String name, CursorFactory
            factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE); //创建Province表
        db.execSQL(CREATE_CITY); //创建City表
        db.execSQL(CREATE_COUNTY); //创建County表
        db.execSQL(CREATE_CURWEATHERINFO);
        db.execSQL(CREATE_RECENTWEATHERINFO);
        db.execSQL(CREATE_WEATHERINDEXES);
        db.execSQL(CREATE_CITYMANAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
