package com.example.materialweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.materialweather.R;
import com.example.materialweather.db.MaterialWeatherDB;
import com.example.materialweather.model.CityManage;

import java.util.List;

/**
 * Created by GavinAndre on 2016/5/17.
 */
public class ManageAreaActivity extends AppCompatActivity {
    private MyListAdapter adapter;
    private ListView myListView;
    private MaterialWeatherDB mMaterialWeatherDB;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_city_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("城市管理");
        }
        setSupportActionBar(toolbar);

        mMaterialWeatherDB = MaterialWeatherDB.getInstance(this);

        adapter = new MyListAdapter(getCityList());
        myListView = (ListView) findViewById(R.id.list_view);
        if (myListView != null) {
            myListView.setAdapter(adapter);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.setCityList(getCityList());
            adapter.notifyDataSetChanged();
        }
//        Log.e("onResume", "start");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_add) {
            Intent intent = new Intent(this, ChooseAreaActivity.class);
            startActivity(intent);
            /*intent.putStringArrayListExtra("allCityNameList",
                    (ArrayList<CityManage>) WeatherActivity.allCityList);
            startActivityForResult(intent, 1);*/
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private List<CityManage> getCityList() {
        return mMaterialWeatherDB.loadCityManage();
    }

    //保存城市列表
    private void deleteCityList(CityManage cityManage) {
        mMaterialWeatherDB.deleteCityManage(cityManage.getCode());
    }


    public class MyListAdapter extends BaseAdapter {
        private List<CityManage> cityManages;

        public MyListAdapter(List<CityManage> cityManages) {
            this.cityManages = cityManages;
        }

        public void setCityList(List<CityManage> cityManages) {
            this.cityManages = cityManages;
        }

        @Override
        public int getCount() {
            if (cityManages != null)
                return cityManages.size();
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (cityManages != null)
                return cityManages.get(position);
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            if (convertView == null) {
                view = View.inflate(ManageAreaActivity.this, R.layout.item_city_list, null);
            } else {
                view = convertView;
            }
            final CityManage cityManage = cityManages.get(position);
            TextView cityName = (TextView) view.findViewById(R.id.cityName);
            ImageView clearBtn = (ImageView) view.findViewById(R.id.clearBtn);
            cityName.setText(cityManage.getName());
            clearBtn.setOnClickListener(new ImageView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cityManages.remove(cityManage);
                    deleteCityList(cityManage);
                    notifyDataSetChanged();
                }
            });
            return view;
        }
    }

}