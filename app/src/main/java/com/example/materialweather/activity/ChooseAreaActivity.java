package com.example.materialweather.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.materialweather.R;
import com.example.materialweather.db.MaterialWeatherDB;
import com.example.materialweather.model.City;
import com.example.materialweather.model.County;
import com.example.materialweather.model.Province;
import com.example.materialweather.util.HttpCallbackListener;
import com.example.materialweather.util.HttpUtil;
import com.example.materialweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GavinAndre on 2015/11/17 0017.
 */
public class ChooseAreaActivity extends AppCompatActivity {

    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private MaterialWeatherDB mMaterialWeatherDB;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中的城市
     */
    private City selectedCity;
    /**
     * 当前选中的级别
     */
    private int currentLevel;
    /**
     * 是否从WeatherActivity中跳转过来
     */
    private boolean isFromWeatherActivity;

    /**
     * Toolbar
     */
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*isFromWeatherActivity = getIntent().getBooleanExtra(
                "from_weather_activity", false);
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);
        //已经选择了城市且不是从WeatherActivity中跳转过来，才会直接跳转到WeatherActivity
        if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent intent = new Intent(this, WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }*/
        setContentView(R.layout.choose_area);

        toolbar = (Toolbar) findViewById(R.id.toolbar1);

        listView = (ListView) findViewById(R.id.list_view);
//        titleText = (TextView) findViewById(R.id.title_text);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        mMaterialWeatherDB = MaterialWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> age0, View view, int index, long arg3) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(index);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(index);
                    queryCounties();
                } else if (currentLevel == LEVEL_COUNTY) {
                    queryFromServer(countyList.get(index).getCountyCode(), String.valueOf(index));

                    /*Intent intent = new Intent(ChooseAreaActivity.this,
                            WeatherActivity.class);
                    intent.putExtra("county_code", countyCode);
                    startActivity(intent);*/
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国所有的省,优先从数据库查询,如果没有查询到再去服务器上查询
     */
    private void queryProvinces() {
        provinceList = mMaterialWeatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            //titleText.setText("中国");
            toolbar.setTitle("中国");
            setSupportActionBar(toolbar);
            currentLevel = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    /**
     * 查询选中省内所有的市,优先从数据库查询,如果没有查询到再去服务器上查询
     */
    private void queryCities() {
        cityList = mMaterialWeatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
//            titleText.setText(selectedProvince.getProvinceName());
            toolbar.setTitle(selectedProvince.getProvinceName());
            setSupportActionBar(toolbar);
            currentLevel = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    /**
     * 查询选中室内所有的县,优先从数据库查询,如果没有查询到再去服务器上查询
     */
    private void queryCounties() {
        countyList = mMaterialWeatherDB.loadCounties(selectedCity.getId());
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
//            titleText.setText(selectedCity.getCityName());
            toolbar.setTitle(selectedCity.getCityName());
            setSupportActionBar(toolbar);
            currentLevel = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "county");
        }

    }

    private void saveCode(String code) {

    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县数据
     */
    private void queryFromServer(final String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.i("Choose_response", response);
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(mMaterialWeatherDB, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(mMaterialWeatherDB, response,
                            selectedProvince.getId());
                } else if ("county".equals(type)) {
                    result = Utility.handleCountiesResponse(mMaterialWeatherDB, response,
                            selectedCity.getId());
                } else {
                    result = Utility.handleCodeResponse(mMaterialWeatherDB, response,
                            countyList.get(Integer.valueOf(type)).getCountyName());
                }
                Log.i("result", "------------------" + result);
                if (result) {
                    //通过runOnUiThread()方法回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("county".equals(type)) {
                                queryCounties();
                            } else if (type != null) {
                                finish();
                            }
                        }
                    });
                } else throw new NullPointerException();
            }

            @Override
            public void onError(Exception e) {
                // 通过runOnUiThread()方法回到主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,
                                "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获Back按键,根据当前的级别来判断,此时应该返回式列表、省列表、还是直接退出
     */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_COUNTY) {
            queryCities();
        } else if (currentLevel == LEVEL_CITY) {
            queryProvinces();
        } else {
            finish();
        }
    }
}
