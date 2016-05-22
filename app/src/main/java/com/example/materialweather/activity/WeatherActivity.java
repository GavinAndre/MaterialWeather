package com.example.materialweather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.materialweather.R;
import com.example.materialweather.adapter.MyAdapter;
import com.example.materialweather.db.MaterialWeatherDB;
import com.example.materialweather.model.CityManage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GavinAndre on 2015/11/18 0018.
 */
public class WeatherActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    private List<CityManage> allCityList;
    private LinearLayout weatherInfoLayout;
    private MaterialWeatherDB mMaterialWeatherDB;
    private MyAdapter mMyAdapter;
    private Toolbar toolbar;
    private ViewPager mViewPager;
    private DrawerLayout drawer;
    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView ivImage;
    private TabLayout tabLayout;
    private NavigationView navigationView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMaterialWeatherDB = MaterialWeatherDB.getInstance(this);
        allCityList = new ArrayList<>();
        allCityList = mMaterialWeatherDB.loadCityManage();

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        if (collapsingToolbar != null) {
            collapsingToolbar.setTitle("Weather");
        }

        ivImage = (ImageView) findViewById(R.id.banner);
        if (ivImage != null) {
            ivImage.setImageResource(R.mipmap.sunrise);
        }

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        //tabLayout.addTab(tabLayout.newTab());
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(this);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        allCityList = mMaterialWeatherDB.loadCityManage();
        setupViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);
    }
    private void setupViewPager(ViewPager mViewPager) {
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());
        if (allCityList.size() > 0) {
            for (int i = 0; i < allCityList.size(); i++) {
                Log.e("setupViewPager", allCityList.get(i).getCode());
                Log.e("setupViewPager", allCityList.get(i).getName());
                adapter.addFragment(DetailFragment.newInstance(
                        allCityList.get(i).getCode()), allCityList.get(i).getName());
                mViewPager.setAdapter(adapter);
            }
        } else {
            adapter.addFragment(DetailFragment.newInstance("101020100"), "上海");
            mViewPager.setAdapter(adapter);
        }
    }

    static class MyPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }

    /**
     * Called when an item in the navigation menu is selected.
     *
     * @param item The selected item
     * @return true to display the item as the selected item
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_item_select_city:
                Intent intent = new Intent(this, ManageAreaActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                /*SharedPreferences prefs = PreferenceManager
                        .getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code", "");
                if (!TextUtils.isEmpty(weatherCode)) {
                    queryRecentWeatherInfo(weatherCode);
                    Snackbar.make(v, "同步成功", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }*/
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.select_city) {
            Intent intent = new Intent(this, ChooseAreaActivity.class);
            intent.putExtra("from_weather_activity", true);
            startActivity(intent);
            //finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
